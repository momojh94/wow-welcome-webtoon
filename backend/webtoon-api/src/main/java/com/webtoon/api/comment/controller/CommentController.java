package com.webtoon.api.comment.controller;

import com.webtoon.api.common.ApiResponse;
import com.webtoon.core.comment.dto.CommentCreateRequest;
import com.webtoon.core.comment.dto.CommentResponse;
import com.webtoon.core.comment.dto.CommentsResponse;
import com.webtoon.core.comment.dto.MyPageCommentsResponse;
import com.webtoon.core.comment.service.CommentService;
import com.webtoon.core.user.domain.User;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments")
    public ApiResponse<CommentsResponse> findComments(@PathVariable("epIdx") Long epIdx,
                                                      @RequestParam("page") int page) {
        return ApiResponse.succeed(commentService.findComments(epIdx, page));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/episodes/{epIdx}/comments/best")
    public ApiResponse<List<CommentResponse>> findBestComments(@PathVariable("epIdx") Long epIdx) {
        return ApiResponse.succeed(commentService.findBestComments(epIdx));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/comments")
    public ApiResponse<MyPageCommentsResponse> findMyPageComments(@AuthenticationPrincipal User user,
                                                                  @RequestParam("page") int page) {
        return ApiResponse.succeed(commentService.findMyPageComments(user, page));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/episodes/{epIdx}/comments")
    public ApiResponse<Void> create(@AuthenticationPrincipal User user,
                                    @PathVariable("epIdx") Long epIdx,
                                    @RequestBody @Valid CommentCreateRequest request) {
        commentService.create(user, epIdx, request.getContent());
        return ApiResponse.succeed();
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/comments/{cmtIdx}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal User user,
                                    @PathVariable("cmtIdx") Long commentIdx) {
        commentService.delete(user, commentIdx);
        return ApiResponse.succeed();
    }
}
