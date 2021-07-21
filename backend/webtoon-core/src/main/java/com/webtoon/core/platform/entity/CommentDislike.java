package com.webtoon.core.platform.entity;

import com.webtoon.core.auth.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CommentDislike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_idx")
    private Comment comment;

    @Builder
    public CommentDislike(Long idx, User user, Comment comment){
        this.idx = idx;
        this.user = user;
        this.comment = comment;
    }
}