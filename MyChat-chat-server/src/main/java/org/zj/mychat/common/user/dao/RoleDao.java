package org.zj.mychat.common.user.dao;

import org.zj.mychat.common.user.domain.entity.Role;
import org.zj.mychat.common.user.mapper.RoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/1720400789">zj</a>
 * @since 2024-02-19
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
