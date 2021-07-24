package com.webtoon.core.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentsResponse {

    private List<CommentResponse> comments;
    private int totalPages;

    @Builder
    public CommentsResponse(List<CommentResponse> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }
}
