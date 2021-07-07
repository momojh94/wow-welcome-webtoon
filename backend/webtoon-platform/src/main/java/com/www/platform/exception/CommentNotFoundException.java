package com.www.platform.exception;

import com.www.core.common.ResourceNotFoundException;

public class CommentNotFoundException extends ResourceNotFoundException {
    public CommentNotFoundException(CommentExceptionStatus status) {
        super(status.getCode(), status.getMsg());
    }
}
