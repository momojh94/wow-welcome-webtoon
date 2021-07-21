package com.webtoon.api.comment.dto;

import com.webtoon.core.comment.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
public class MyPageCommentResponseDto {

    private Long idx;
    private String webtoonThumbnail;
    private String webtoonTitle;
    private int epNo;
    private int likeCount;
    private int dislikeCount;
    private String content;
    private String createdDate;

    public MyPageCommentResponseDto(Comment entity) {
        idx = entity.getIdx();
        webtoonThumbnail = "http://localhost:8081/static/web_thumbnail/" + entity.getEp().getWebtoon().getThumbnail();
        webtoonTitle = entity.getEp().getWebtoon().getTitle();
        epNo = entity.getEp().getEpNo();
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