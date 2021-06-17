package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.file.repository.WebtoonRepository;
import com.www.core.platform.entity.StarRating;
import com.www.core.platform.repository.StarRatingRepository;
import com.www.platform.dto.EpisodeStarRatingResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Response<EpisodeStarRatingResponseDto> insertStarRating(Long epIdx, Long userIdx, float rating) {
        Response<EpisodeStarRatingResponseDto> result = new Response<EpisodeStarRatingResponseDto>();

        if(starRatingRepository.existsByEpIdxAndUserIdx(epIdx, userIdx))
        {
            result.setCode(26);
            result.setMsg("fail : have already given star rating");
        }
        else
        {
            Optional<User> user = userRepository.findById(userIdx);
            Optional<Episode> episode = episodeRepository.findById(epIdx);

            if(!episode.isPresent()){ // 에피소드가 존재하지 않을 때
                result.setCode(20);
                result.setMsg("fail : episode doesn't exist");
                return result;
            }

            StarRating starRating = StarRating.builder()
                    .user(user.get())
                    .ep(episode.get())
                    .rating(rating)
                    .build();
            starRatingRepository.save(starRating);

            episodeRepository.updateRatingAvgAndPersonTotal(epIdx);
            webtoonRepository.updateRatingAvg(episode.get().getWebtoon().getIdx());

            episode = episodeRepository.findById(epIdx);

            EpisodeStarRatingResponseDto episodeStarRatingResponseDto
                    = EpisodeStarRatingResponseDto.builder()
                    .rating(episode.get().getRatingAvg())
                    .personTotal(episode.get().getRatingPersonTotal())
                    .build();

            result.setCode(0);
            result.setMsg("request complete : insert star rating");
            result.setData(episodeStarRatingResponseDto);
        }

        return result;
    }
}
