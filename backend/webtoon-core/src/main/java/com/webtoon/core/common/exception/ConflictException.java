package com.webtoon.core.common.exception;

public class ConflictException extends CustomException {

    public ConflictException(String message, String errorCode) {
        super(message, errorCode);
    }

}