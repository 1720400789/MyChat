package org.zj.mychat.common.common.aspect;

import io.micrometer.core.instrument.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.zj.mychat.common.common.annotation.RedissonLock;
import org.zj.mychat.common.common.service.LockService;
import org.zj.mychat.common.common.utils.SpringELUtils;

import java.lang.reflect.Method;

/**
 * 注解式分布式锁工具类的切面类
 */
@Component
@Aspect
@Order(0) // 这个注解非常重要，确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {

    @Autowired
    private LockService lockService;

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix = StringUtils.isBlank(redissonLock.prefixKey()) ? SpringELUtils.getMethodKey(method) : redissonLock.prefixKey();
        String key = SpringELUtils.parseSpEL(method, joinPoint.getArgs(), redissonLock.key());
        return lockService.executeWithLock(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
