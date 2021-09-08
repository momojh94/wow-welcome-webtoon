package com.webtoon.core.comment.service;


import com.webtoon.core.comment.domain.StarRating;
import com.webtoon.core.comment.repository.StarRatingRepository;
import com.webtoon.core.comment.dto.EpisodeStarRatingResponse;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.episode.repository.EpisodeRepository;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.webtoon.repository.WebtoonRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.webtoon.core.common.exception.ExceptionType.EPISODE_NOT_FOUND;
import static com.webtoon.core.common.exception.ExceptionType.USER_HAVE_ALREADY_RATED_EPISODE;

@Service
public class StarRatingService {
    private EpisodeRepository episodeRepository;
    private WebtoonRepository webtoonRepository;
    private StarRatingRepository starRatingRepository;

    public StarRatingService(EpisodeRepository episodeRepository,
                             WebtoonRepository webtoonRepository,
                             StarRatingRepository starRatingRepository) {
        this.episodeRepository = episodeRepository;
        this.webtoonRepository = webtoonRepository;
        this.starRatingRepository = starRatingRepository;
    }

    @Transactional
    public EpisodeStarRatingResponse create(Long epIdx, User user, float rating) {
        if (starRatingRepository.existsByEpIdxAndUser(epIdx, user)) {
            throw USER_HAVE_ALREADY_RATED_EPISODE.getException();
        }

        Episode episode = episodeRepository.findById(epIdx)
                                           .orElseThrow(EPISODE_NOT_FOUND::getException);

        StarRating starRating = StarRating.builder()
                                          .user(user)
                                          .ep(episode)
                                          .rating(rating)
                                          .build();

        starRatingRepository.save(starRating);

        episodeRepository.updateRatingAvgAndPersonTotal(epIdx);
        webtoonRepository.updateRatingAvg(episode.getWebtoon().getIdx());

        EpisodeStarRatingResponse result
                = EpisodeStarRatingResponse.builder()
                                           .ratingAvg(episode.getRatingAvg())
                                           .personTotal(episode.getRatingPersonTotal())
                                           .build();

        return result;
    }
}
