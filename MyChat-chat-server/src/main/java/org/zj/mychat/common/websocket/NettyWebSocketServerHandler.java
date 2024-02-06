package org.zj.mychat.common.websocket;

import cn.hutool.json.JSONUtil;
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
import org.zj.mychat.common.websocket.domain.vo.resp.WSBaseResp;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.debug("握手完成");
        } else if (evt instanceof IdleStateEvent) {
            /* 下面是心跳捕捉后的处理逻辑 */
            // 将 evt 强转为【空闲事件对象】
            IdleStateEvent event = (IdleStateEvent) evt;
            // 如果是读空闲事件
            if (event.state() == IdleState.READER_IDLE) {
                // TODO 则说明客户端心跳停止，做出相关【用户下线】的工作
                log.debug("读空闲发生");
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case AUTHORIZE:
                break;
            case HEARTBEAT:
                break;
            case LOGIN:
                ctx.channel().writeAndFlush(new TextWebSocketFrame("123"));
        }
    }
}
