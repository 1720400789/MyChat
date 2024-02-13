package org.zj.mychat.common.websocket.service.adapter;

import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.websocket.domain.enums.WSRespTypeEnum;
import org.zj.mychat.common.websocket.domain.vo.resp.WSBaseResp;
import org.zj.mychat.common.websocket.domain.vo.resp.WSLoginSuccess;
import org.zj.mychat.common.websocket.domain.vo.resp.WSLoginUrl;

/**
 * Adapter 类
 * 即适配器类，专职做一些组装实体类的逻辑，保证 service 层的逻辑清晰
 */
public class WebSocketAdapter {

    /**
     *
     * @param wxMpQrCodeTicket 微信 API 提供的二维码
     * @return 信息
     */
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return resp;
    }

    /**
     * 包装用户信息，即当用户登录成功后返回的信息
     * @param user 用户信息
     * @param token 用户 token
     * @return 信息
     */
    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess loginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .build();
        resp.setData(loginSuccess);
        return resp;
    }

    /**
     * 包装提示用户授权的信息
     * @return 提示信息
     */
    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    /**
     * 包装提示用户 token 过期的信息
     * @return 提示信息
     */
    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return resp;
    }
}
