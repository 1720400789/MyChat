package org.zj.mychat.common.common.event.listener;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.zj.mychat.common.common.event.UserRegisterEvent;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.enums.IdepotentEnum;
import org.zj.mychat.common.user.domain.enums.ItemEnum;
import org.zj.mychat.common.user.service.UserBackpackService;

/**
 * 用户注册事件监听器
 */
@Component
public class UserRegisterListener {

    @Autowired
    private UserBackpackService userBackpackService;

    @Autowired
    private UserDao userDao;

//    @EventListener(classes = UserRegisterEvent.class)
    @Async // 异步执行
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT) // 事件对象为 UserRegisterEvent.class，事务类型为事务提交后执行
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdepotentEnum.UID, user.getId().toString());
    }

    @Async // 异步执行
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT) // 事件对象为 UserRegisterEvent.class，事务类型为事务提交后执行
    public void sendBadge(UserRegisterEvent event) {
        // 前 100 名的注册徽章
        User user = event.getUser();
        int registeredCount = userDao.count();
        if (registeredCount < 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdepotentEnum.UID, user.getId().toString());
        } else if (registeredCount < 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdepotentEnum.UID, user.getId().toString());
        }
    }
}
