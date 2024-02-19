package org.zj.mychat.common.websocket.service;

import cn.hutool.core.net.url.UrlBuilder;
import io.micrometer.core.instrument.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.zj.mychat.common.websocket.NettyUtil;

import java.net.InetSocketAddress;
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
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
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
            // 取用户 ip
            String ip = request.headers().get("X-Real-IP");
            if (StringUtils.isBlank(ip)) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            // 将 ip 保存到 channel 附件
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            // 获取 token 和 ip 的处理器只需要处理一次，删除是为了避免每次请求都执行一遍这个逻辑
            ctx.pipeline().remove(this);
        }
        ctx.fireChannelRead(msg);
    }
}

