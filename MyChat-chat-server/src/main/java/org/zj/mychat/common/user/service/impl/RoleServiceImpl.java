package org.zj.mychat.common.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zj.mychat.common.user.domain.enums.RoleEnum;
import org.zj.mychat.common.user.service.RoleService;
import org.zj.mychat.common.user.service.cache.UserCache;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}
