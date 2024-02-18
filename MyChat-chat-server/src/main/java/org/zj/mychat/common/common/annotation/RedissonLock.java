package org.zj.mychat.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 注解式分布式锁工具
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
@Target(ElementType.METHOD) // 作用在方法上
public @interface RedissonLock {

    /**
     * key 的前缀，默认取方法全限定类名，可以自己指定
     */
    String prefixKey() default "";

    /**
     * 支持 SpringEL 表达式的 Key
     */
    String key();

    /**
     * 获取锁的等待时间，默认为 -1，即快速失败，失败一次后不等待继续尝试
     */
    int waitTime() default -1;

    /**
     * 等待时间的单位，默认毫秒
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
