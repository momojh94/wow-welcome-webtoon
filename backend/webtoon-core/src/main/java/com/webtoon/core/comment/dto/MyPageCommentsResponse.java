package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageCommentsResponse {
    private List<MyPageCommentResponse> comments;
    private int totalPages;

    @Builder
    private MyPageCommentsResponse(List<MyPageCommentResponse> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }

    public static MyPageCommentsResponse of(List<MyPageCommentResponse> comments, int totalPages) {
        return MyPageCommentsResponse.builder()
                                     .comments(comments)
                                     .totalPages(totalPages)
                                     .build();
    }
}
