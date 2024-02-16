package org.zj.mychat.common.common.exception;

import lombok.Data;

/**
 * 自定义业务异常类
 */
@Data
public class BussinessException extends RuntimeException {

    protected Integer errorCode;

    protected String errorMsg;

    public BussinessException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = CommonErrorEnum.BUSSINESS_ERROR.getErrorCode();
    }

    public BussinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }
}
