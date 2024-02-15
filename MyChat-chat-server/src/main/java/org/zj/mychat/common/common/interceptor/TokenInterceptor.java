package org.zj.mychat.common.common.interceptor;

import cn.hutool.http.ContentType;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.zj.mychat.common.common.exception.HttpErrorEnum;
import org.zj.mychat.common.user.service.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * 自定义登录拦截器
 * 实现 Spring 的 HandlerInterceptor 接口
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String UID = "uid";

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        // 如果用户存在登录态
        if (Objects.nonNull(validUid)) {
            request.setAttribute(UID, validUid);
        } else {
            // 如果不存在登录态
            // 1. 先检查请求路径是否带有 /public 前缀，否则直接打回请求
            boolean isPublicURI = isPublicURI(request);
            if (!isPublicURI) {
                // 返回 401
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;
    }

    private boolean isPublicURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        return split.length > 2 && "public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
