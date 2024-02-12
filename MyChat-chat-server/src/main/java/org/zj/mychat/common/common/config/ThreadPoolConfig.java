package org.zj.mychat.common.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.zj.mychat.common.common.thread.MyThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {
    /**
     * 项目共用线程池
     */
    public static final String MYCHAT_EXECUTOR = "mychatExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return mychatExecutor();
    }

    @Bean(MYCHAT_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor mychatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 开启线程池优雅停机，即停机前会阻止向线程池提交任务，并阻塞等待执行完线程池中的任务
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程池中线程名前缀
        executor.setThreadNamePrefix("mychat-executor-");
        // 配置线程池拒绝策略，如果队列满则要求提交任务的线程自己执行任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 通过配置线程工厂来增强原生线程池，使其线程对象的异常能以日志的形式保存下来
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    // 如何引用 ？
//    @Autowired
//    @Qualifier(ThreadPoolConfig.MYCHAT_EXECUTOR)
//    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

}
