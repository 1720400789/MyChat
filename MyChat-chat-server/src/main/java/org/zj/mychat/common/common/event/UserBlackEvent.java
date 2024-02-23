package org.zj.mychat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.zj.mychat.common.user.domain.entity.User;

/**
 * 用户登录事件
 */
@Getter
public class UserBlackEvent extends ApplicationEvent {

    private User user;

    public UserBlackEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
