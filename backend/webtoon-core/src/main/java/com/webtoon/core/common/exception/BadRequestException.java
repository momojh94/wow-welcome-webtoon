package com.webtoon.core.common.exception;


public class BadRequestException extends CustomException {

    public BadRequestException (String errorCode, String message) {
        super(errorCode, message);
    }

}