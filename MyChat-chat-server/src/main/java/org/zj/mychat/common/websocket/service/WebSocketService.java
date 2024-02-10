package org.zj.mychat.common.websocket.service;

import io.netty.channel.Channel;

public interface WebSocketService {
    /**
     * 客户端 WS 连接建立成功后保存连接
     * @param channel 用户连接对象
     */
    void connect(Channel channel);

    /**
     * 保存在线用户连接与登录二维码的关系
     * @param channel 用户连接对象
     */
    void handleLoginReq(Channel channel);

    /**
     * 封装用户下线逻辑
     * @param channel 待处理的已经下线的用户连接
     */
    void remove(Channel channel);

    /**
     * 登录成功逻辑
     * @param code
     * @param id
     */
    void scanLoginSuccess(Integer code, Long id);

    /**
     * 等待授权
     * @param code
     */
    void waitAuthorize(Integer code);
}
