package com.www.core.common;


import lombok.Getter;

// errorCode를 포함한 CustomRuntimeException

@Getter
public class CustomRuntimeException extends RuntimeException{
    private final String errorCode;

    public CustomRuntimeException(final String errorCode, final String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
    }
}
