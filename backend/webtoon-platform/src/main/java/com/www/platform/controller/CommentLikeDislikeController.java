package com.www.platform.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.platform.dto.CommentLikeDislikeCountResponseDto;
import com.www.platform.service.CommentLikeDislikeService;
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
    public Response<CommentLikeDislikeCountResponseDto> requestCommentLike(@RequestHeader("Authorization") String accessToken,
                                                                           @PathVariable("cmtIdx") Long commentIdx) {
        Response<CommentLikeDislikeCountResponseDto> result = new Response<CommentLikeDislikeCountResponseDto>();

        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentLikeDislikeService.requestLike(userIdx, commentIdx);
                break;
            case 1: // 만료된 토큰
                result.setCode(44);
                result.setMsg("access denied : invalid access token");
                break;
            case 2: // 에러,올바르지 않은 토큰
                result.setCode(42);
                result.setMsg("access denied : maybe captured or faked token");
                break;
        }

        return result;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/comments/{cmtIdx}/dislike")
    public Response<CommentLikeDislikeCountResponseDto> requestCommentDislike(@RequestHeader("Authorization") String accessToken,
                                                                              @PathVariable("cmtIdx") Long commentIdx) {
        Response<CommentLikeDislikeCountResponseDto> result = new Response<CommentLikeDislikeCountResponseDto>();

        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentLikeDislikeService.requestDislike(userIdx, commentIdx);
                break;
            case 1: // 만료된 토큰
                result.setCode(44);
                result.setMsg("access denied : invalid access token");
                break;
            case 2: // 에러,올바르지 않은 토큰
                result.setCode(42);
                result.setMsg("access denied : maybe captured or faked token");
                break;
        }

        return result;
    }
}
