package com.www.api.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageCommentsResponseDto {
    private List<MyPageCommentResponseDto> comments;
    private int totalPages;

    @Builder
    public MyPageCommentsResponseDto(List<MyPageCommentResponseDto> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }
}
