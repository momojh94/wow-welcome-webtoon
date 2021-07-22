package com.webtoon.core.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageCommentsResponse {
    private List<MyPageCommentResponse> comments;
    private int totalPages;

    @Builder
    public MyPageCommentsResponse(List<MyPageCommentResponse> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }
}
