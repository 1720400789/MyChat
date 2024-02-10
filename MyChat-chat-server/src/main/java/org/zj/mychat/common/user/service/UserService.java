package org.zj.mychat.common.user.service;

import org.zj.mychat.common.user.domain.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-07
 */
public interface UserService {

    Long register(User insert);
}
