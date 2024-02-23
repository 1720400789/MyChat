package org.zj.mychat.common.user.service;

import org.zj.mychat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-19
 */
public interface RoleService {

    /**
     * 用户是否拥有某种权限
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);

}
