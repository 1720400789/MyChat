package org.zj.mychat.common.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zj.mychat.common.common.domain.vo.resp.ApiResult;

/**
 * 全局异常捕获类
 * @RestControllerAdvice 注解，表示捕获所有 Rest Controller 类抛出的异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        return ApiResult.fail(CommonErrorEnum.PARAM_INVALID.getCode(), message.substring(0, message.length() - 1));
    }

    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> methodArgumentNotValidException(Throwable e) {
        log.error("system exception! The reason is: {}", e.getMessage());
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(value = BussinessException.class)
    public ApiResult<?> bussinessException(BussinessException e) {
        log.info("system exception! The reason is: {}", e.getMessage());
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }
}
