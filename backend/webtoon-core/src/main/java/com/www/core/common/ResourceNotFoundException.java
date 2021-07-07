package com.www.core.common;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    private final String errorCode;

    public ResourceNotFoundException(final String errorCode, final String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
    }
}
