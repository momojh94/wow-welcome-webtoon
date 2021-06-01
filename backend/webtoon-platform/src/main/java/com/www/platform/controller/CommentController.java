package com.www.platform.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.platform.dto.*;
import com.www.platform.service.CommentLikeDislikeService;
import com.www.platform.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CommentController {
    private CommentService commentService;
    private CommentLikeDislikeService commentLikeDislikeService;
    private TokenChecker tokenChecker;

    @GetMapping("/episodes/{epIdx}/comments")
    public Response<CommentsResponseDto> getComments(@PathVariable("epIdx") Long epIdx,
                                                     @RequestParam("page") int page) {
        return commentService.getCommentsByPageRequest(epIdx, page);
    }

    @PostMapping("/episodes/{epIdx}/comments")
    public Response<Long> insertComment(@RequestHeader("Authorization") String accessToken,
                                        @PathVariable("epIdx") Long epIdx,
                                        @RequestBody CommentSaveRequestDto dto) {
        Response<Long> result = new Response<Long>();

        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentService.insertComment(userIdx, epIdx, dto.getContent());
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

    @DeleteMapping("/comments/{cmtIdx}")
    public Response<Long> deleteComment(@RequestHeader("Authorization") String accessToken,
                                        @PathVariable("cmtIdx") Long commentIdx) {
        Response<Long> result = new Response<Long>();

        switch (tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentService.deleteComment(userIdx, commentIdx);
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

    @GetMapping("/episodes/{epIdx}/comments/best")
    public Response<List<CommentDto>> getBestComments(@PathVariable("epIdx") Long epIdx) {
        return commentService.getBestComments(epIdx);
    }

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

    @GetMapping("/users/comments")
    public Response<MyPageCommentsResponseDto> getMyPageComments(@RequestHeader("Authorization") String accessToken,
                                                                 @RequestParam("page") int page) {
        Response<MyPageCommentsResponseDto> result = new Response<MyPageCommentsResponseDto>();

        switch(tokenChecker.validateToken(accessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(accessToken);
                if(-1 == userIdx){
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentService.getMyPageComments(userIdx, page);
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
