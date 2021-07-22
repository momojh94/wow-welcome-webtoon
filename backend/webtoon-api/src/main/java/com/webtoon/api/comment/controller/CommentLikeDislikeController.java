package com.webtoon.api.comment.controller;

import com.webtoon.api.common.ApiResponse;
import com.webtoon.core.user.service.TokenChecker;
import com.webtoon.core.comment.dto.CommentLikeDislikeCountResponseDto;
import com.webtoon.core.comment.service.CommentLikeDislikeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentLikeDislikeController {
    private final CommentLikeDislikeService commentLikeDislikeService;
    private final TokenChecker tokenChecker;

    public CommentLikeDislikeController(CommentLikeDislikeService commentLikeDislikeService,
                                        TokenChecker tokenChecker) {
        this.commentLikeDislikeService = commentLikeDislikeService;
        this.tokenChecker = tokenChecker;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/comments/{cmtIdx}/like")
    public ApiResponse<CommentLikeDislikeCountResponseDto> requestCommentLike(@RequestHeader("Authorization") String accessToken,
                                                                              @PathVariable("cmtIdx") Long commentIdx) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (-1 == userIdx) {
                    break;
                }
                return ApiResponse.succeed(commentLikeDislikeService.requestLike(userIdx, commentIdx));
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/comments/{cmtIdx}/dislike")
    public ApiResponse<CommentLikeDislikeCountResponseDto> requestCommentDislike(@RequestHeader("Authorization") String accessToken,
                                                                                 @PathVariable("cmtIdx") Long commentIdx) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (-1 == userIdx) {
                    break;
                }
                return ApiResponse.succeed(commentLikeDislikeService.requestDislike(userIdx, commentIdx));
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }
}
