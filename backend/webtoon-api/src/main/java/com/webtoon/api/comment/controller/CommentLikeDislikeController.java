package com.webtoon.api.comment.controller;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.comment.dto.CommentLikeDislikeCountResponse;
import com.webtoon.core.comment.service.CommentLikeDislikeService;
import com.webtoon.core.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentLikeDislikeController {

    private final CommentLikeDislikeService commentLikeDislikeService;

    public CommentLikeDislikeController(CommentLikeDislikeService commentLikeDislikeService) {
        this.commentLikeDislikeService = commentLikeDislikeService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/comments/{cmtIdx}/like")
    public ApiResponse<CommentLikeDislikeCountResponse> requestCommentLike(@AuthenticationPrincipal User user,
                                                                           @PathVariable("cmtIdx") Long commentIdx) {
        return ApiResponse.succeed(commentLikeDislikeService.requestLike(user, commentIdx));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/comments/{cmtIdx}/dislike")
    public ApiResponse<CommentLikeDislikeCountResponse> requestCommentDislike(@AuthenticationPrincipal User user,
                                                                              @PathVariable("cmtIdx") Long commentIdx) {
        return ApiResponse.succeed(commentLikeDislikeService.requestDislike(user, commentIdx));
    }
}
