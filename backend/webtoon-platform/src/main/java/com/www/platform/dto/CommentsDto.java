package com.www.platform.dto;

import com.www.core.platform.entity.Comments;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
public class CommentsDto {
    private Long idx;
    private String user_id;
    private int like_cnt;
    private int dislike_cnt;
    private String content;
    private String created_date;

    public CommentsDto(Comments entity) {
        idx = entity.getIdx();
        user_id = entity.getUser().getAccount();
        like_cnt = entity.getLikeCount();
        dislike_cnt = entity.getDislikeCount();
        content = entity.getContent();
        created_date = toStringDateTime(entity.getCreatedDate());
    }

    private String toStringDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Optional.ofNullable(localDateTime)
                .map(formatter::format)
                .orElse("");
    }
}