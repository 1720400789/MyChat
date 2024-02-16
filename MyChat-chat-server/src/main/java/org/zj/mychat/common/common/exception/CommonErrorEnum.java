package org.zj.mychat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {

    BUSSINESS_ERROR(0, "{}"),
    SYSTEM_ERROR(-1, "系统开小差了，请稍后再试"),
    PARAM_INVALID(-2, "参数校验失败"),

    ;

    private final Integer code;

    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}