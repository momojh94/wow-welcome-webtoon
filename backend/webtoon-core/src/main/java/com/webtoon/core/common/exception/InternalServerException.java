package com.webtoon.core.common.exception;

public class InternalServerException extends CustomException {

    public InternalServerException(String errorCode, String message) {
        super(errorCode, message);
    }

}