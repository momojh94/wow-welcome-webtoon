package com.www.platform.exception;

import com.www.core.common.BadRequestException;

public class CommentBadReqeustException extends BadRequestException {
    public CommentBadReqeustException(CommentExceptionStatus status) {
        super(status.getCode(), status.getMsg());
    }
}