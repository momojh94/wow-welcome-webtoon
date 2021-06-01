package com.www.platform.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageCommentsResponseDto {
    private List<MyPageCommentDto> comments;
    private int totalPages;

    @Builder
    public MyPageCommentsResponseDto(List<MyPageCommentDto> comments, int totalPages) {
        this.comments = comments;
        this.totalPages = totalPages;
    }
}
