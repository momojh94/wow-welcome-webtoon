package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentLikeDislikeCountResponse {

    private int count;

    @Builder
    private CommentLikeDislikeCountResponse(int count) {
        this.count = count;
    }

    public static CommentLikeDislikeCountResponse of (int count) {
        return CommentLikeDislikeCountResponse.builder()
                                              .count(count)
                                              .build();
    }
}
