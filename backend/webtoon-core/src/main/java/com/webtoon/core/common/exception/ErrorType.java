package com.webtoon.core.common.exception;

public enum ErrorType {

    // 공통
    INVALID_REQUEST_VALUE(400, "C000", "적절하지 않은 요청 값 처리"),

    // 댓글
    COMMENT_NOT_FOUND(404, "CMT000", "댓글이 존재하지 않습니다."),
    USER_IS_NOT_COMMENTER(400, "CMT001", "User는 해당 댓글의 작성자가 아닙니다."),
    BAD_COMMENT_LIKE_REQUEST(400, "CMT002", "User 자신이 쓴 댓글에 좋아요 요청을 할 수 없습니다"),
    BAD_COMMENT_DISLIKE_REQUEST(400, "CMT003", "User 자신이 쓴 댓글에 싫어요 요청을 할 수 없습니다"),

    // User
    USER_NOT_FOUND(404, "U000", "유저가 존재하지 않습니다."),

    // 에피소드
    EPISODE_NOT_FOUND(404, "EP000", "에피소드(회차)가 존재하지 않습니다."),
    USER_HAVE_ALREADY_RATED_EPISODE(400, "EP001", "유저는 이미 해당 에피소드에 별점을 등록했습니다.");


    private final int status;
    private final String code;
    private final String message;

    ErrorType(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
