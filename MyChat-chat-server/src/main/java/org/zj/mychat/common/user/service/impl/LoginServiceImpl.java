package org.zj.mychat.common.user.service.impl;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zj.mychat.common.common.constant.RedisKey;
import org.zj.mychat.common.common.utils.JwtUtils;
import org.zj.mychat.common.common.utils.RedisUtils;
import org.zj.mychat.common.user.service.LoginService;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Async // Spring 提供的异步方法注解，使用 @Async 注解的方法会被 Spring 用适配器包装一层，就实现无感知地异步处理了
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if (expireDays == -2) {
            // 如果 key 不存在
            return ;
        }
        // 刷新 token 时间
        if (expireDays < TOKEN_EXPIRE_DAYS) {
            RedisUtils.expire(getUserTokenKey(uid), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);

        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return null;
        }
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        if (StringUtils.isBlank(oldToken)) {
            return null;
        }
        return Objects.equals(oldToken, token) ? uid : null;
    }

    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}
