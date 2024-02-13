package org.zj.mychat.common.websocket.service;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * 自定义的握手处理器，即 http 协议升级成 ws 协议的处理器
 * 顺便还要获取原 http 中请求头中的 token
 * 但是不推荐这样写
 */
public class MyHandShakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final HttpObject httpObject = (HttpObject) msg;

        if (httpObject instanceof HttpRequest) {
            // 1、从 http 请求中获取 token
            final HttpRequest req = (HttpRequest) httpObject;
            String token = req.headers().get("Sec-WebSocket-Protocol");
            // 2、向 channel 中添加 token 作为附件
            Attribute<Object> token1 = ctx.channel().attr(AttributeKey.valueOf("token"));
            token1.set(token);
            // 3、握手，http 协议升级为 WS 协议
            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    req.getUri(),
                    token, false);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                // Ensure we set the handshaker and replace this handler before we
                // trigger the actual handshake. Otherwise we may receive websocket bytes in this handler
                // before we had a chance to replace it.
                //
                // See https://github.com/netty/netty/issues/9471.
                ctx.pipeline().remove(this);

                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (!future.isSuccess()) {
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            // Kept for compatibility
                            ctx.fireUserEventTriggered(
                                    WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                        }
                    }
                });
            }
        } else
            ctx.fireChannelRead(msg);
    }
}

