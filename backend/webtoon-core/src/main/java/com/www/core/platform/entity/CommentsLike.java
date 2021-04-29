package com.www.core.platform.entity;

import com.www.core.auth.entity.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CommentsLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "users_idx")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "comments_idx")
    private Comments comment;

    @Builder
    public CommentsLike(Long idx, Users user, Comments comment){
        this.idx = idx;
        this.user = user;
        this.comment = comment;
    }
}