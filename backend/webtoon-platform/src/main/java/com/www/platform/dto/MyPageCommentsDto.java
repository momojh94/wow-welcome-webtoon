package com.www.platform.dto;

import com.www.core.platform.entity.Comments;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
public class MyPageCommentsDto {

    private Long idx;
    private String webtoon_thumbnail;
    private String webtoon_title;
    private int ep_no;
    private int like_cnt;
    private int dislike_cnt;
    private String content;
    private String created_date;

    public MyPageCommentsDto(Comments entity) {
        idx = entity.getIdx();
        webtoon_thumbnail = "http://localhost:8081/static/web_thumbnail/" + entity.getEp().getWebtoon().getThumbnail();
        webtoon_title = entity.getEp().getWebtoon().getTitle();
        ep_no = entity.getEp().getEpNo();
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