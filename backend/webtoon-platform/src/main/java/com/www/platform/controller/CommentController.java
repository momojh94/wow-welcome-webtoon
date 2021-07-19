package com.www.platform.controller;

import com.www.core.common.ApiResponse;
import com.www.core.common.service.TokenChecker;
import com.www.platform.dto.CommentDto;
import com.www.platform.dto.CommentSaveRequestDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.dto.MyPageCommentsResponseDto;
import com.www.platform.service.CommentService;
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
        return ApiResponse.of(commentService.getCommentsByPageRequest(epIdx, page));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments/best")
    public ApiResponse<List<CommentDto>> getBestComments(@PathVariable("epIdx") Long epIdx) {
        return ApiResponse.of(commentService.getBestComments(epIdx));
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
                return ApiResponse.of(commentService.getMyPageComments(userIdx, page));
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
                                    @RequestBody CommentSaveRequestDto dto) {
        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (userIdx == -1) {
                    break;
                }
                commentService.create(userIdx, epIdx, dto.getContent());
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
