package com.webtoon.core.comment.dto;

import com.webtoon.core.episode.domain.Episode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeStarRatingResponse {

    private float ratingAvg;
    private int personTotal;

    @Builder
    private EpisodeStarRatingResponse(float ratingAvg, int personTotal) {
        this.ratingAvg = ratingAvg;
        this.personTotal = personTotal;
    }

    public static EpisodeStarRatingResponse of(Episode episode) {
        return EpisodeStarRatingResponse.builder()
                                        .ratingAvg(episode.getRatingAvg())
                                        .personTotal(episode.getRatingPersonTotal())
                                        .build();
    }
}


