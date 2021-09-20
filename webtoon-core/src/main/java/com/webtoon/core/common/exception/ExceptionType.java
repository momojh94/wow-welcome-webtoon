package com.webtoon.core.common.exception;

public enum ExceptionType {

    // 공통
    INVALID_REQUEST_VALUE(new BadRequestException("C000", "요청한 데이터들의 값이 유효하지 않습니다.")),
    INTERNAL_SERVER(new InternalServerException("C001", "서버 에러입니다. 서버 관리자에게 문의해주세요.")),

    // 댓글
    COMMENT_NOT_FOUND(new NotFoundException("CMT000", "댓글이 존재하지 않습니다.")),
    USER_IS_NOT_COMMENTER(new BadRequestException("CMT001", "User는 해당 댓글의 작성자가 아닙니다.")),
    BAD_COMMENT_LIKE_REQUEST(new BadRequestException("CMT002", "User 자신이 쓴 댓글에 좋아요 요청을 할 수 없습니다")),
    BAD_COMMENT_DISLIKE_REQUEST(new BadRequestException("CMT003", "User 자신이 쓴 댓글에 싫어요 요청을 할 수 없습니다")),

    // 유저
    USER_NOT_FOUND(new NotFoundException("U000", "유저가 존재하지 않습니다.")),
    ALREADY_JOINED_ACCOUNT(new ConflictException("U001", "이미 가입된 계정입니다.")),
    
    // 인증
    LOGIN_REQUIRED(new UnauthorizedException("A000", "로그인이 필요합니다.")),
    ALREADY_LOGOUT(new UnauthorizedException("A001", "이미 로그아웃 됐습니다.")),
    WRONG_PASSWORD(new UnauthorizedException("A003", "잘못된 비밀번호 입니다.")),
    INVALID_TOKEN(new UnauthorizedException("A004", "유효하지 않은 토큰입니다")),
    EXPIRED_TOKEN(new UnauthorizedException("A005", "만료된 토큰 입니다.")),

    // 에피소드(회차)
    EPISODE_NOT_FOUND(new NotFoundException("EP000", "에피소드(회차)가 존재하지 않습니다.")),
    USER_HAVE_ALREADY_RATED_EPISODE(new BadRequestException("EP001", "유저는 이미 해당 에피소드에 별점을 등록했습니다.")),

    // 웹툰
    WEBTOON_NOT_FOUND(new NotFoundException("WT000", "웹툰이 존재하지 않습니다.")),
    USER_IS_NOT_AUTHOR_OF_WEBTOON(new BadRequestException("WT001", "유저는 해당 웹툰의 작가가 아닙니다."));

    private final CustomException exception;

    ExceptionType(final CustomException exception) {
        this.exception = exception;
    }

    public CustomException getException() {
        return this.exception;
    }

    public String getMessage() {
        return exception.getMessage();
    }

    public String getErrorCode() {
        return exception.getErrorCode();
    }
}
