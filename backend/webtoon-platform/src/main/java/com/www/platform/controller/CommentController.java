package com.www.platform.controller;

import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.platform.dto.CommentDto;
import com.www.platform.dto.CommentSaveRequestDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.dto.MyPageCommentsResponseDto;
import com.www.platform.service.CommentLikeDislikeService;
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
    private final CommentLikeDislikeService commentLikeDislikeService;
    private final TokenChecker tokenChecker;

    public CommentController(CommentService commentService,
                             CommentLikeDislikeService commentLikeDislikeService,
                             TokenChecker tokenChecker) {
        this.commentService = commentService;
        this.commentLikeDislikeService = commentLikeDislikeService;
        this.tokenChecker = tokenChecker;
    }

    @PostMapping("/episodes/{epIdx}/comments")
    public Response<Long> createComment(@RequestHeader("Authorization") String accessToken,
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments")
    public Response<CommentsResponseDto> getComments(@PathVariable("epIdx") Long epIdx,
                                                     @RequestParam("page") int page) {
        return commentService.getCommentsByPageRequest(epIdx, page);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments/best")
    public Response<List<CommentDto>> getBestComments(@PathVariable("epIdx") Long epIdx) {
        return commentService.getBestComments(epIdx);
    }

    @ResponseStatus(HttpStatus.OK)
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

    @ResponseStatus(HttpStatus.OK)
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


}
