package com.webtoon.core.common.exception;

public class NotFoundException extends CustomException {

    public NotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }

}