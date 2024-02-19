package org.zj.mychat.common.common.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.zj.mychat.common.common.event.UserOnlineEvent;
import org.zj.mychat.common.common.event.UserRegisterEvent;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.enums.UserActiveStatusEnum;
import org.zj.mychat.common.user.service.IpService;

/**
 * 用户登录成功事件监听器
 */
@Component
public class UserOnlineListener {

    @Autowired
    private IpService ipService;

    @Autowired
    private UserDao userDao;

    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void sendDB(UserOnlineEvent event) {
        User user = event.getUser();
        User update = User.builder()
                .id(user.getId())
                .lastOptTime(user.getLastOptTime())
                .ipInfo(user.getIpInfo())
                .activeStatus(UserActiveStatusEnum.ONLINE.getStatus())
                .build();
        userDao.updateById(update);
        // 用户 ip 详情的解析
        ipService.refreshIpDetailAsync(user.getId());
    }
}
