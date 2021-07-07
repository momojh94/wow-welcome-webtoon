package com.www.platform.exception;

import lombok.Getter;

@Getter
public enum CommentExceptionStatus {
    NOT_FOUND("CMT_000", "댓글이 존재하지 않습니다."),
    IS_NOT_COMMENTER("CMT_001", "User가 해당 댓글의 작성자가 아닙니다."),
    BAD_COMMENT_LIKE_REQUEST("CMT_002", "User 자신이 쓴 댓글에 좋아요 요청을 할 수 없습니다"),
    BAD_COMMENT_DISLIKE_REQUEST("CMT_003", "User 자신이 쓴 댓글에 싫어요 요청을 할 수 없습니다");

    private final String code;
    private final String msg;

    CommentExceptionStatus(final String code, final String msg) {
        this.code = code;
        this.msg = msg;
    }
}
