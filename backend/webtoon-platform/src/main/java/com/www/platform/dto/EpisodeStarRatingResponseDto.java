package com.www.platform.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeStarRatingResponseDto {
    float ratingAvg;
    int personTotal;

    @Builder
    public EpisodeStarRatingResponseDto(float ratingAvg, int personTotal) {
        this.ratingAvg = ratingAvg;
        this.personTotal = personTotal;
    }
}


