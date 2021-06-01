package com.www.platform.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentsResponseDto {
    private List<CommentDto> comments;
    private int totalPages;

    @Builder
    public CommentsResponseDto(List<CommentDto> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }
}
