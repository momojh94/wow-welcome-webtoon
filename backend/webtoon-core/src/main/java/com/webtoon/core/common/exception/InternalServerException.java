package com.webtoon.core.common.exception;

public class InternalServerException extends CustomException {

    public InternalServerException(String message, String errorCode) {
        super(message, errorCode);
    }

}