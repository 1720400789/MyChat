package org.zj.mychat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.zj.mychat.common.websocket.domain.enums.WSReqTypeEnum;
import org.zj.mychat.common.websocket.domain.vo.req.WSBaseReq;
import org.zj.mychat.common.websocket.service.WebSocketService;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * WebSocketService 的实现类在 Spring 容器的管理中，但是当前 handler 不在 Spring 容器中
     * 所以我们不能直接使用 Spring 注入，有两种方式
     * 第一种是我们自己写一个 Spring 容器上下文类，利用 Spring 提供的 ApplicationContextAware
     * 第二种是直接使用第三方库，例如 hutool 就提供了 SpringUtil 的类，其中的 getBean 可以拿到 Spring 管理的 Bean
     */
    private WebSocketService webSocketService;

    /**
     * 客户端建立 WS 连接成功的钩子函数，这个钩子函数是不同于下面的 channelRead0 方法的：
     *      channelActive 一旦客户端 WS 连接建立成功即触发，而 channelRead0 方法是 pipeLine 上的处理工序，专门处理通过 WS 连接发送的消息
     * 这里我们需要保存每个客户端连接的 channel
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        // 保存 channel 连接
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffline(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.debug("握手完成");
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
        } else if (evt instanceof IdleStateEvent) {
            /* 下面是心跳捕捉后的处理逻辑 */
            // 将 evt 强转为【空闲事件对象】
            IdleStateEvent event = (IdleStateEvent) evt;
            // 如果是读空闲事件
            if (event.state() == IdleState.READER_IDLE) {
                // TODO 则说明客户端心跳停止，做出相关【用户下线】的工作
                log.debug("读空闲发生");
                userOffline(ctx.channel());
                // 关闭连接
                ctx.channel().close();
            }
        }
    }

    /**
     * 封装用户下线逻辑
     */
    private void userOffline(Channel channel) {
        webSocketService.remove(channel);
        channel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 自定义处理通过 WS 连接发送的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case AUTHORIZE:
                webSocketService.authorize(ctx.channel(), wsBaseReq.getData());
            case HEARTBEAT:
                break;
            case LOGIN:
                // 保存
                webSocketService.handleLoginReq(ctx.channel());
        }
    }
}
