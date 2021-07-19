package com.www.core.common.exception;


public class BusinessException extends CustomException{
    public BusinessException(ErrorType errorType) {
        super(errorType);
    }
}