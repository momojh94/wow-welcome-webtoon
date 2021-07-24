package com.webtoon.core.comment.domain;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.episode.domain.Episode;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class StarRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "ep_idx", nullable = false)
    private Episode ep;

    @ManyToOne
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @Column(nullable = false)
    private float rating;

    @Builder
    public StarRating(Episode ep, User user, float rating){
        this.ep = ep;
        this.user = user;
        this.rating = rating;
    }
}


