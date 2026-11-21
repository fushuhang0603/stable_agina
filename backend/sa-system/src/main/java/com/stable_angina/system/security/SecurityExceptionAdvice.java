package com.stable_angina.system.security;

import com.stable_angina.common.api.ErrorCodes;
import com.stable_angina.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 方法级鉴权异常处理（@PreAuthorize 抛出），避免 sa-common 依赖 spring-security
 */
@Slf4j
@RestControllerAdvice
public class SecurityExceptionAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.fail(ErrorCodes.FORBIDDEN, "无权限访问");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthentication(AuthenticationException e) {
        return Result.fail(ErrorCodes.UNAUTHORIZED, "未登录或登录已过期");
    }
}
