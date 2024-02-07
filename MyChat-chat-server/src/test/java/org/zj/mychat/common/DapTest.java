package org.zj.mychat.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DapTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void test() {
        User byId = userDao.getById(1);
    }
}
