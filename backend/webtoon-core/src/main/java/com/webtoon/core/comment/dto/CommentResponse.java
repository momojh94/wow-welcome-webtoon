package com.webtoon.core.comment.dto;


import com.webtoon.core.comment.domain.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.webtoon.core.common.exception.ExceptionType.INTERNAL_SERVER;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private Long idx;
    private String account;
    private int likeCount;
    private int dislikeCount;
    private String content;
    private String createdDate;

    @Builder
    private CommentResponse(Long idx, String account, int likeCount,
                            int dislikeCount, String content, String createdDate) {
        this.idx = idx;
        this.account = account;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.content = content;
        this.createdDate = createdDate;
    }

    private static String toStringDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(localDateTime);
    }

    public static CommentResponse of(Comment comment){
        return CommentResponse.builder()
                              .idx(comment.getIdx())
                              .account(comment.getUser().getAccount())
                              .likeCount(comment.getLikeCount())
                              .dislikeCount(comment.getDislikeCount())
                              .content(comment.getContent())
                              .createdDate(toStringDateTime(comment.getCreatedDate()))
                              .build();

    }
}