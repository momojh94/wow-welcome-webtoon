package com.webtoon.api.comment.service;

import com.webtoon.core.auth.entity.User;
import com.webtoon.core.auth.repository.UserRepository;
import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.file.entity.Episode;
import com.webtoon.core.file.repository.EpisodeRepository;
import com.webtoon.core.file.repository.WebtoonRepository;
import com.webtoon.core.platform.entity.StarRating;
import com.webtoon.core.platform.repository.StarRatingRepository;
import com.webtoon.api.comment.dto.EpisodeStarRatingResponseDto;
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
    public EpisodeStarRatingResponseDto createStarRating(Long epIdx, Long userIdx, float rating) {
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

        EpisodeStarRatingResponseDto result
                = EpisodeStarRatingResponseDto.builder()
                                              .ratingAvg(episode.getRatingAvg())
                                              .personTotal(episode.getRatingPersonTotal())
                                              .build();

        return result;
    }
}
