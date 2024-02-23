package org.zj.mychat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.zj.mychat.common.common.event.UserOnlineEvent;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.IpInfo;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.enums.RoleEnum;
import org.zj.mychat.common.user.service.LoginService;
import org.zj.mychat.common.user.service.RoleService;
import org.zj.mychat.common.user.service.UserRoleService;
import org.zj.mychat.common.websocket.NettyUtil;
import org.zj.mychat.common.websocket.domain.dto.WSChannelExtraDTO;
import org.zj.mychat.common.websocket.domain.vo.resp.WSBaseResp;
import org.zj.mychat.common.websocket.service.WebSocketService;
import org.zj.mychat.common.websocket.service.adapter.WebSocketAdapter;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门管理 WS 的逻辑实现类，包括推拉
 */
@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleService roleService;

    /**
     * 引入我们自定义的线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public static final Duration DURATION = Duration.ofMinutes(5);
    public static final int MAXIMUM_SIZE = 1000;
    /**
     * 考虑到我们的业务无法感知到微信二维码是否被使用过，所以我们不方便释放 Map 的内存，如果还是使用原生的 ConcurrentHashMap 来存储关系的话，由于业务中没有合适的时机释放 Map 内存，所以在大用户量和长时间部署的环境下出现【内存溢出】的问题
     * 所以要求这个 Map 能够设置元素上限，能够设置元素过期时间来防止 OOM
     * 利用框架 caffeine 提供的本地缓存 Map 集合来存储 WS 连接与登录 code 的映射关系
     * 这里使用 Integer 是因为在下面 handleLoginReq 的业务逻辑中，微信二维码对应唯一的数字串
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            // 设置缓存 Map 的最大元素数量，达到上限会自动清理，防止内存溢出
            .maximumSize(MAXIMUM_SIZE)
            // 设置元素的过期时间
            .expireAfterWrite(DURATION)
            .build();

    /**
     * 利用线程安全的 ConcurrentHashMap 来存储 WS 连接 channel 和 请求数据对象的关系
     * 管理所有在线用户（已登录 和 游客）的连接
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 保存 channel 连接
     * @param channel
     */
    @Override
    public void connect(Channel channel) {
        // 保存连接，因为当前无法确定用户登录状态，所以这里保存一个空对象
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 保存在线用户连接与登录二维码的关系
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        // 1. 生成随机码
        Integer code = generateLoginCode(channel);
        // 2. 携带随机码并向微信申请二维码
        // - 参数一，唯一 code
        // - 参数二，二维码过期时间
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        // 3. 把二维码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    /**
     * 用户下线逻辑
     */
    @Override
    public void remove(Channel channel) {
        // 将已经下线的用户连接从 Map 中清除
        ONLINE_WS_MAP.remove(channel);
        // TODO 用户下线
    }

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        // 确认授权的用户的链接正常
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return ;
        }
        User user = userDao.getById(uid);
        // 移除 code
        WAIT_LOGIN_MAP.invalidate(code);
        // 调用登录模块获取 token
        String token = loginService.login(uid);
        // 用户登录
        loginSuccess(channel, user, token);
    }

    /**
     *
     * @param code
     */
    @Override
    public void waitAuthorize(Integer code) {
        // 确认待授权的用户的链接正常
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return ;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            // 如果 token 存在，则说明用户登录可用
            User user = userDao.getById(validUid);
            // 登录成功处理的逻辑
            loginSuccess(channel, user, token);
            // 提示用户登录成功
//            sendMsg(channel, WebSocketAdapter.buildResp(user, token));
        } else {
            // 如果 token 不存在，则提示前端用户登录已过期
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    /**
     * 向所有在线的用户推送消息
     * @param msg 待推送的消息
     */
    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {
        // ONLEINS_WS_MAP 中存储着所有当前在线的用户的 channel 连接
        ONLINE_WS_MAP.forEach(((channel, wsChannelExtraDTO) -> {
            threadPoolTaskExecutor.execute(() -> {
                sendMsg(channel, msg);
            });
        }));
    }

    private void loginSuccess(Channel channel, User user, String token) {
        // 保存 channel 的对应 uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        // 推送成功的消息
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        // 用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode(Channel channel) {
        Integer code;
        // 循环生成在 Map 中唯一的 code 键
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }

}
