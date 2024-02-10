package org.zj.mychat.common.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.service.UserService;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        // TODO 用户注册的事件
        return insert.getId();
    }
}
