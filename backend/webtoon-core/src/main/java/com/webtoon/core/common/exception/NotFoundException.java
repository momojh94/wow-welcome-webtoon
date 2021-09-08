package com.webtoon.core.common.exception;

public class NotFoundException extends CustomException {

    public NotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }

}