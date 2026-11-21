package com.stable_angina.common.exception;

import com.stable_angina.common.api.BusinessException;
import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：业务异常、参数校验异常、兜底异常
 * 说明：鉴权相关异常（AccessDeniedException 等）在 sa-system 的 SecurityExceptionAdvice 中处理，
 *      避免 sa-common 依赖 spring-security
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBiz(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleValid(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("参数校验失败");
        return Result.fail(ErrorCodes.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ErrorCodes.SERVER_ERROR, "系统异常，请稍后重试");
    }
}
