package com.www.platform.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.platform.dto.*;
import com.www.platform.service.CommentsLikeDislikeService;
import com.www.platform.service.CommentsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CommentsController {

    private CommentsService commentsService;
    private CommentsLikeDislikeService commentsLikeDislikeService;
    private TokenChecker tokenChecker;

    @GetMapping("/episodes/{epIdx}/comments")
    public Response<CommentsResponseDto> getComments(@PathVariable("epIdx") Long epIdx,
                                                     @RequestParam("page") int page) {
        return commentsService.getCommentsByPageRequest(epIdx, page);
    }

    @PostMapping("/episodes/{epIdx}/comments")
    public Response<Long> insertComments(@RequestHeader("Authorization") String AccessToken,
                                         @PathVariable("epIdx") Long epIdx,
                                         @RequestBody CommentsSaveRequestDto dto) {
        Response<Long> result = new Response<Long>();

        switch (tokenChecker.validateToken(AccessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(AccessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentsService.insertComments(userIdx, epIdx, dto.getContent());
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
    public Response<Long> deleteComments(@RequestHeader("Authorization") String AccessToken,
                                         @PathVariable("cmtIdx") Long commentIdx) {
        Response<Long> result = new Response<Long>();

        switch (tokenChecker.validateToken(AccessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(AccessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentsService.deleteComments(userIdx, commentIdx);
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
    public Response<List<CommentsDto>> getBestComments(@PathVariable("epIdx") Long epIdx) {
        return commentsService.getBestComments(epIdx);
    }

    @PostMapping("/comments/{cmtIdx}/like")
    public Response<CommentsLikeDislikeCntResponseDto> requestCommentsLike(@RequestHeader("Authorization") String AccessToken,
                                                                           @PathVariable("cmtIdx") Long commentIdx) {
        Response<CommentsLikeDislikeCntResponseDto> result = new Response<CommentsLikeDislikeCntResponseDto>();

        switch (tokenChecker.validateToken(AccessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(AccessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentsLikeDislikeService.requestLike(userIdx, commentIdx);
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
    public Response<CommentsLikeDislikeCntResponseDto> requestCommentsDislike(@RequestHeader("Authorization") String AccessToken,
                                                                              @PathVariable("cmtIdx") Long commentIdx) {
        Response<CommentsLikeDislikeCntResponseDto> result = new Response<CommentsLikeDislikeCntResponseDto>();

        switch (tokenChecker.validateToken(AccessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(AccessToken);
                if (-1 == userIdx) {
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentsLikeDislikeService.requestDislike(userIdx, commentIdx);
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
    public Response<MyPageCommentsResponseDto> getMyPageComments(@RequestHeader("Authorization") String AccessToken,
                                                                 @RequestParam("page") int page) {
        Response<MyPageCommentsResponseDto> result = new Response<MyPageCommentsResponseDto>();

        switch(tokenChecker.validateToken(AccessToken)) {
            case 0: // 유효한 토큰
                Long userIdx = tokenChecker.getUserIdx(AccessToken);
                if(-1 == userIdx){
                    result.setCode(42);
                    result.setMsg("access denied : maybe captured or faked token");
                    break;
                }
                result = commentsService.getMyPageComments(userIdx, page);
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
