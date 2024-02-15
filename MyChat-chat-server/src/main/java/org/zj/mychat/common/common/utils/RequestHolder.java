package org.zj.mychat.common.common.utils;

import org.zj.mychat.common.common.domain.dto.RequestInfo;

/**
 * 请求的上下文
 */
public class RequestHolder {

    private static final ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        threadLocal.set(requestInfo);
    }

    public static RequestInfo get() {
        return threadLocal.get();
    }

    /**
     * 使用 ThreadLocal 要记得及时删除
     * 避免内存泄露
     */
    public static void remove() {
        threadLocal.remove();
    }
}
