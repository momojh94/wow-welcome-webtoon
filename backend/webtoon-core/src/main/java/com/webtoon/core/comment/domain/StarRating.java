package com.webtoon.core.comment.domain;


import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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


