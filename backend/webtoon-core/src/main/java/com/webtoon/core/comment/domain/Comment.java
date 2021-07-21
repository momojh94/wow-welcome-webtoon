package com.webtoon.core.comment.domain;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.common.BaseCreatedTimeEntity;
import com.webtoon.core.episode.domain.Episode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(nullable = false, name = "ep_idx")
    private Episode ep;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_idx")
    private User user;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int dislikeCount;

    @Column(columnDefinition = "TEXT", nullable = false, length = 500)
    private String content;

    @Builder
    public Comment(Episode ep, User user, int likeCount, int dislikeCount,
                   Long idx, String content, LocalDateTime createdDate){
        this.ep = ep;
        this.user = user;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.idx = idx;
        this.content = content;
        this.createdDate = createdDate;
    }

    public boolean wasWrittenBy(Long userIdx) {
        return this.user.getIdx() == userIdx;
    }

    public boolean wasWrittenBy(User user) {
        return this.user.getIdx() == user.getIdx();
    }
}


