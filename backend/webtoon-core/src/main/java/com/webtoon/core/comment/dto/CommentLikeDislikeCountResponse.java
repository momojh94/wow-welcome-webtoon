package com.webtoon.core.comment.dto;

import lombok.Getter;

@Getter
public class CommentLikeDislikeCountResponse {

    private int count;

    public CommentLikeDislikeCountResponse(int count) {
        this.count = count;
    }
}
