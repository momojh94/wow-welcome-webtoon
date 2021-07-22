package com.webtoon.core.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeStarRatingRequest {

    @Max(value = 10, message = "별점은 10점을 넘을 수 없습니다")
    private float rating;

    public EpisodeStarRatingRequest(float rating) {
        this.rating = rating;
    }
}