package com.webtoon.core.common.exception;

public class ConflictException extends CustomException {

    public ConflictException(String errorCode, String message) {
        super(errorCode, message);
    }

}