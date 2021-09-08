package com.webtoon.core.common.exception;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode);
    }

}
