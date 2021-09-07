package com.webtoon.core.common.exception;

/**
 * 400 Bad Request
 * 403 Forbidden
 * 404 Not Found
 * 409 Conflict
 */
public enum ErrorType {

    // 공통
    INVALID_REQUEST_VALUE(400, "C000", "적절하지 않은 요청 값 처리"),

    // 댓글
    COMMENT_NOT_FOUND(404, "CMT000", "댓글이 존재하지 않습니다."),
    USER_IS_NOT_COMMENTER(400, "CMT001", "User는 해당 댓글의 작성자가 아닙니다."),
    BAD_COMMENT_LIKE_REQUEST(400, "CMT002", "User 자신이 쓴 댓글에 좋아요 요청을 할 수 없습니다"),
    BAD_COMMENT_DISLIKE_REQUEST(400, "CMT003", "User 자신이 쓴 댓글에 싫어요 요청을 할 수 없습니다"),

    // 유저
    USER_NOT_FOUND(404, "U000", "유저가 존재하지 않습니다."),
    ALREADY_JOINED_ACCOUNT(409, "U001", "이미 가입된 계정입니다."),
    
    // 인증
    LOGIN_REQUIRED(401, "A000", "로그인이 필요합니다."),
    ALREADY_LOGOUT(401, "A001", "이미 로그아웃 됐습니다."),
    WRONG_PASSWORD(401, "A003", "잘못된 비밀번호 입니다."),
    INVALID_TOKEN(401, "A004", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(401, "A005", "만료된 토큰 입니다."),

    // 에피소드(회차)
    EPISODE_NOT_FOUND(404, "EP000", "에피소드(회차)가 존재하지 않습니다."),
    USER_HAVE_ALREADY_RATED_EPISODE(400, "EP001", "유저는 이미 해당 에피소드에 별점을 등록했습니다."),

    // 웹툰
    WEBTOON_NOT_FOUND(404, "WT000", "웹툰이 존재하지 않습니다."),
    USER_IS_NOT_AUTHOR_OF_WEBTOON(400, "WT001", "유저는 해당 웹툰의 작가가 아닙니다.");

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
