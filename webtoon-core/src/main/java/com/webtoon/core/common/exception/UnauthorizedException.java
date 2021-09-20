package com.webtoon.core.common.exception;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message);
    }

}
