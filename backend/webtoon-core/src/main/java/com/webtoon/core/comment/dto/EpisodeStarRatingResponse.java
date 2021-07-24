package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeStarRatingResponse {

    private float ratingAvg;
    private int personTotal;

    @Builder
    public EpisodeStarRatingResponse(float ratingAvg, int personTotal) {
        this.ratingAvg = ratingAvg;
        this.personTotal = personTotal;
    }
}


