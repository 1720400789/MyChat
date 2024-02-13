package org.zj.mychat.common.websocket;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.channel.Channel;

public class NettyUtil {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T value) {
        Attribute<T> attr = channel.attr(key);
        attr.set(value);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }
}
