package com.webtoon.core.comment.dto;

import lombok.Getter;

@Getter
public class CommentLikeDislikeCountResponseDto {

    private int count;

    public CommentLikeDislikeCountResponseDto(int count) {
        this.count = count;
    }
}
