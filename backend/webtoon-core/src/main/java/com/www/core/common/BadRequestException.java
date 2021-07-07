package com.www.core.common;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{
    private final String errorCode;

    public BadRequestException(final String errorCode, final String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
    }
}
