package org.zj.mychat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.zj.mychat.common.user.domain.entity.User;

/**
 * 用户注册事件
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {

    private User user;

    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
