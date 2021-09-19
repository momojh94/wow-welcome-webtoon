package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentsResponse {

    private List<CommentResponse> comments;
    private int totalPages;

    @Builder
    public CommentsResponse(List<CommentResponse> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }

    public static CommentsResponse of(List<CommentResponse> comments, int totalPages){
        return CommentsResponse.builder()
                               .comments(comments)
                               .totalPages(totalPages)
                               .build();
    }
}
