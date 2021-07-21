package com.webtoon.core.comment.dto;

import com.webtoon.core.comment.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
public class CommentResponseDto {

    private Long idx;
    private String account;
    private int likeCount;
    private int dislikeCount;
    private String content;
    private String createdDate;

    public CommentResponseDto(Comment entity) {
        idx = entity.getIdx();
        account = entity.getUser().getAccount();
        likeCount = entity.getLikeCount();
        dislikeCount = entity.getDislikeCount();
        content = entity.getContent();
        createdDate = toStringDateTime(entity.getCreatedDate());
    }

    private String toStringDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Optional.ofNullable(localDateTime)
                .map(formatter::format)
                .orElse("");
    }
}