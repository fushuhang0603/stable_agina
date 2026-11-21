package com.stable_angina.common.exception;

import com.stable_angina.common.api.ErrorCodes;
import lombok.Getter;

/**
 * 业务异常，由 GlobalExceptionHandler 统一转为 Result
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(ErrorCodes.FAIL, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
