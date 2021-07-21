package com.webtoon.api.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeStarRatingResponseDto {

    private float ratingAvg;
    private int personTotal;

    @Builder
    public EpisodeStarRatingResponseDto(float ratingAvg, int personTotal) {
        this.ratingAvg = ratingAvg;
        this.personTotal = personTotal;
    }
}


