package com.webtoon.core.comment.service;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.episode.domain.EpisodeRepository;
import com.webtoon.core.webtoon.domain.WebtoonRepository;
import com.webtoon.core.comment.domain.StarRating;
import com.webtoon.core.comment.domain.StarRatingRepository;
import com.webtoon.core.comment.dto.EpisodeStarRatingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.webtoon.core.common.exception.ErrorType.EPISODE_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.USER_HAVE_ALREADY_RATED_EPISODE;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;

@Service
public class StarRatingService {
    private UserRepository userRepository;
    private EpisodeRepository episodeRepository;
    private WebtoonRepository webtoonRepository;
    private StarRatingRepository starRatingRepository;

    public StarRatingService(UserRepository userRepository,
                             EpisodeRepository episodeRepository,
                             WebtoonRepository webtoonRepository,
                             StarRatingRepository starRatingRepository) {
        this.userRepository = userRepository;
        this.episodeRepository = episodeRepository;
        this.webtoonRepository = webtoonRepository;
        this.starRatingRepository = starRatingRepository;
    }

    @Transactional
    public EpisodeStarRatingResponse create(Long epIdx, Long userIdx, float rating) {
        if (starRatingRepository.existsByEpIdxAndUserIdx(epIdx, userIdx)) {
            throw new ApplicationException(USER_HAVE_ALREADY_RATED_EPISODE);
        }

        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
        Episode episode = episodeRepository.findById(epIdx)
                                           .orElseThrow(() -> new ApplicationException(EPISODE_NOT_FOUND));

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
