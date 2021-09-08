package com.webtoon.core.comment.dto;

import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.common.util.DateUtils;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageCommentResponse {

    private Long idx;
    private String webtoonThumbnail;
    private String webtoonTitle;
    private int epNo;
    private int likeCount;
    private int dislikeCount;
    private String content;
    private String createdDate;

    @Builder
    private MyPageCommentResponse(Long idx, String webtoonThumbnail, String webtoonTitle,
                                 int epNo, int likeCount, int dislikeCount, String content,
                                 String createdDate) {
        this.idx = idx;
        this.webtoonThumbnail = webtoonThumbnail;
        this.webtoonTitle = webtoonTitle;
        this.epNo = epNo;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.content = content;
        this.createdDate = createdDate;
    }

    public static MyPageCommentResponse of(Comment comment) {
        Episode episode = comment.getEp();
        Webtoon webtoon = episode.getWebtoon();
        String webtoonThumbnail = "http://localhost:8080/static/web_thumbnail/" + webtoon.getThumbnail();
        return MyPageCommentResponse.builder()
                                    .idx(comment.getIdx())
                                    .webtoonThumbnail(webtoonThumbnail)
                                    .webtoonTitle(webtoon.getTitle())
                                    .epNo(episode.getEpNo())
                                    .likeCount(comment.getLikeCount())
                                    .dislikeCount(comment.getDislikeCount())
                                    .content(comment.getContent())
                                    .createdDate(DateUtils.toStringDateTime(comment.getCreatedDate()))
                                    .build();
    }
}