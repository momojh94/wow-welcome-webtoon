package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.exception.BusinessException;
import com.www.core.file.entity.Episode;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.file.repository.WebtoonRepository;
import com.www.core.platform.entity.StarRating;
import com.www.core.platform.repository.StarRatingRepository;
import com.www.platform.dto.EpisodeStarRatingResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.www.core.common.exception.ErrorType.EPISODE_NOT_FOUND;
import static com.www.core.common.exception.ErrorType.USER_HAVE_ALREADY_RATED_EPISODE;
import static com.www.core.common.exception.ErrorType.USER_NOT_FOUND;

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
            throw new BusinessException(USER_HAVE_ALREADY_RATED_EPISODE);
        }

        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        Episode episode = episodeRepository.findById(epIdx)
                                           .orElseThrow(() -> new BusinessException(EPISODE_NOT_FOUND));

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
