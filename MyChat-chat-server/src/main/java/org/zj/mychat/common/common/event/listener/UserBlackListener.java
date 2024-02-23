package org.zj.mychat.common.common.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.zj.mychat.common.common.event.UserBlackEvent;
import org.zj.mychat.common.common.event.UserOnlineEvent;
import org.zj.mychat.common.user.dao.UserDao;
import org.zj.mychat.common.user.domain.entity.User;
import org.zj.mychat.common.user.domain.enums.UserActiveStatusEnum;
import org.zj.mychat.common.user.service.IpService;
import org.zj.mychat.common.user.service.cache.UserCache;
import org.zj.mychat.common.websocket.service.WebSocketService;
import org.zj.mychat.common.websocket.service.adapter.WebSocketAdapter;

/**
 * 用户登录成功事件监听器
 */
@Component
public class UserBlackListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserCache userCache;

    /**
    * 向所有在线用户推送黑名单信息
    */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent event) {
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    /**
     * 更新用户状态为拉黑
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStates(UserBlackEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }

    /**
     * 清除黑名单缓存
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(UserBlackEvent event) {
        userCache.evictBlackMap();
    }
}
