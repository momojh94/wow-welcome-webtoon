package com.webtoon.api.comment.controller;

import com.webtoon.api.common.ApiResponse;
import com.webtoon.core.common.service.TokenChecker;
import com.webtoon.core.comment.dto.CommentCreateRequestDto;
import com.webtoon.core.comment.dto.CommentResponseDto;
import com.webtoon.core.comment.dto.CommentsResponseDto;
import com.webtoon.core.comment.dto.MyPageCommentsResponseDto;
import com.webtoon.core.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final TokenChecker tokenChecker;

    public CommentController(CommentService commentService,
                             TokenChecker tokenChecker) {
        this.commentService = commentService;
        this.tokenChecker = tokenChecker;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments")
    public ApiResponse<CommentsResponseDto> getComments(@PathVariable("epIdx") Long epIdx,
                                                        @RequestParam("page") int page) {
        return ApiResponse.succeed(commentService.getCommentsByPageRequest(epIdx, page));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments/best")
    public ApiResponse<List<CommentResponseDto>> getBestComments(@PathVariable("epIdx") Long epIdx) {
        return ApiResponse.succeed(commentService.getBestComments(epIdx));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/comments")
    public ApiResponse<MyPageCommentsResponseDto> getMyPageComments(@RequestHeader("Authorization") String accessToken,
                                                                    @RequestParam("page") int page) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (userIdx == -1) {
                    break;
                }
                return ApiResponse.succeed(commentService.getMyPageComments(userIdx, page));
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/episodes/{epIdx}/comments")
    public ApiResponse<Void> create(@RequestHeader("Authorization") String accessToken,
                                    @PathVariable("epIdx") Long epIdx,
                                    @RequestBody @Valid CommentCreateRequestDto request) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (userIdx == -1) {
                    break;
                }
                commentService.create(userIdx, epIdx, request.getContent());
                return ApiResponse.succeed();
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/comments/{cmtIdx}")
    public ApiResponse<Void> deleteComment(@RequestHeader("Authorization") String accessToken,
                                           @PathVariable("cmtIdx") Long commentIdx) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (userIdx == -1) {
                    break;
                }
                commentService.delete(userIdx, commentIdx);
                return ApiResponse.succeed();
            case 1: // 만료된 토큰
                return ApiResponse.fail("44", "access denied : invalid access token");
            default:
        }

        return ApiResponse.fail("42", "access denied : maybe captured or faked token");
    }
}
