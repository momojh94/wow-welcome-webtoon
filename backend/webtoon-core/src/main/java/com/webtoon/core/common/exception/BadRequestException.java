package com.webtoon.core.common.exception;


public class BadRequestException extends CustomException {

    public BadRequestException (String message, String errorCode) {
        super(message, errorCode);
    }

}