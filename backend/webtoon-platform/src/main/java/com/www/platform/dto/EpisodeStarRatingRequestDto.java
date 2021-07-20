package com.www.platform.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeStarRatingRequestDto {
    float rating;

    public EpisodeStarRatingRequestDto(float rating) {
        this.rating = rating;
    }
}