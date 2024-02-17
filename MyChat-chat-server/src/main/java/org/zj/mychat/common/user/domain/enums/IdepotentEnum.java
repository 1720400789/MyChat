package org.zj.mychat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 幂等枚举类
 */
@Getter
@AllArgsConstructor
public enum IdepotentEnum {

    UID(1, "uid"),
    MSG_ID(2, "消息 id"),
    ;

    private final Integer type;

    private final String desc;

}
