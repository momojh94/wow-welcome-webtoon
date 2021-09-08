package com.webtoon.core.comment.dto;


import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.common.util.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static CommentResponse of(Comment comment){
        return CommentResponse.builder()
                              .idx(comment.getIdx())
                              .account(comment.getUser().getAccount())
                              .likeCount(comment.getLikeCount())
                              .dislikeCount(comment.getDislikeCount())
                              .content(comment.getContent())
                              .createdDate(DateUtils.toStringDateTime(comment.getCreatedDate()))
                              .build();

    }
}