package com.enerpulse.common.exception;

import com.enerpulse.common.api.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(ErrorCode.BUSINESS_ERROR, message);
    }
}
