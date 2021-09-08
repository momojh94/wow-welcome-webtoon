package com.webtoon.core.common.exception;

public class ApplicationException extends RuntimeException{
    private final ExceptionType exceptionType;

    public ApplicationException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public ExceptionType getErrorType() {
        return exceptionType;
    }
}