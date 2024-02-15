package org.zj.mychat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.zj.mychat.common.common.domain.dto.RequestInfo;
import org.zj.mychat.common.common.utils.RequestHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.UID))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
        String ip = ServletUtil.getClientIP(request);
        RequestInfo requestInfo = RequestInfo.builder()
                .uid(uid)
                .ip(ip)
                .build();
        RequestHolder.set(requestInfo);
        return true;
    }

    /**
     * 及时移除 ThreadLocal
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
