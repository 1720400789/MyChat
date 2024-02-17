package org.zj.mychat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.service.UserService;
import org.zj.mychat.common.user.service.WXMsgService;
import org.zj.mychat.common.user.service.adapter.TextBuilder;
import org.zj.mychat.common.user.service.adapter.UserAdapter;
import org.zj.mychat.common.websocket.service.WebSocketService;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WXMsgServiceImpl implements WXMsgService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Autowired
    private WebSocketService webSocketService;

    @Value("${wx.mp.callback}")
    private String callback;

    /**
     * 微信 API 要求的授权成功回调 URL
     */
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    /**
     * 同维护在 WebSocketService 中的两个 Map 一样，这个 Map 用户保存用户的 openid 和 登录 eventKey 的映射关系
     */
    private static final ConcurrentHashMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)) {
            return null;
        }
        // 通过 openId 查询用户是否已经注册过了
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        // 用户已经注册并授权
        if (registered && authorized) {
            // 则推送用户信息，进入登录成功逻辑，通过 code 给 channel 推送消息
            webSocketService.scanLoginSuccess(code, user.getId());
        }
        // 没有登录成功
        // 情况一：如果是没有注册，则为用户注册
        if (!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        // 情况二：如果是没有授权，则推送链接让用户授权
        // 扫码事件处理
        WAIT_AUTHORIZE_MAP.put(openId, code);
        // 需要授权时返回信息给用户，提醒用户
        webSocketService.waitAuthorize(code);
        // 拼接微信用户授权的 URL
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));

        // 返回公众号内消息给用户，如果用户点击链接并授权，则微信会帮我们将请求重定向至 /callback 回调函数
        return TextBuilder.build("请点击登录：<a href=\"" + authorizeUrl + "\">登录</a>", wxMpXmlMessage);
    }

    /**
     * 用户授权
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        // 更新用户信息
        if (StrUtil.isBlank(user.getAvatar())) {
            fillUserInfo(user.getId(), userInfo);
        }
        // 通过 code 找到用户 channel 进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        // 授权成功进入登录成功逻辑
        webSocketService.scanLoginSuccess(code, user.getId());
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("getEventKey error eventKey: {}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }
}
