package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeStarRatingRequest {

    @Min(value = 0)
    @Max(value = 10)
    private float rating;

    public EpisodeStarRatingRequest(float rating) {
        this.rating = rating;
    }
}