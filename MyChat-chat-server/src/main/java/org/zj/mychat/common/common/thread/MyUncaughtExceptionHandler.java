package org.zj.mychat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义线程池线程对象异常捕获器
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread", e);
    }
}
