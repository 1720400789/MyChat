package org.zj.mychat.common.websocket.service;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.zj.mychat.common.websocket.NettyUtil;

import java.util.Optional;

/**
 * http 请求处理器，目的同 MyHandShakeHandler，都是为了拿到 http 请求中携带的 token
 * 但是当前 handler 比较优雅，更加符合规范一些，而 MyHandShakeHandler 管得太多了，处了处理 http 请求外还要搞 http 协议升级的事情
 */
public class MyHeaderCollectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            // 从 http 请求中获取 token
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.getUri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            /*
            if (tokenOptional.isPresent()) {
                NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, tokenOptional.get());
            }
             */
            // 如果 token 存在则将其设置入 channel 的附件
            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            // 因为 uri 的任务只是携带 token，为了之后责任链上的握手处理器能够正确处理，所以这里将请求的路径改为去掉参数后的路径
            request.setUri(urlBuilder.getPath().toString());
        }
        ctx.fireChannelRead(msg);
    }
}

