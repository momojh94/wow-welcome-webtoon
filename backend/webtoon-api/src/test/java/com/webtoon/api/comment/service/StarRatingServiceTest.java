package com.webtoon.api.comment.service;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import com.webtoon.core.user.domain.UserRepository;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.episode.domain.EpisodeRepository;
import com.webtoon.core.webtoon.domain.WebtoonRepository;
import com.webtoon.core.comment.domain.StarRating;
import com.webtoon.core.comment.domain.StarRatingRepository;
import com.webtoon.api.comment.dto.EpisodeStarRatingResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
public class StarRatingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private WebtoonRepository webtoonRepository;

    @Mock
    private StarRatingRepository starRatingRepository;

    @InjectMocks
    private StarRatingService starRatingService;

    private User user;
    private Webtoon webtoon;
    private Episode episode;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .idx(1L)
                .account("id1")
                .name("철수")
                .pw("1q2w3e4r")
                .gender(Gender.MALE)
                .email("test@email.com")
                .build();

        webtoon = Webtoon.builder()
                .idx(1L)
                .title("웹툰 제목")
                .storyType(StoryType.EPISODE)
                .storyGenre1(StoryGenre.DAILY)
                .storyGenre2(StoryGenre.GAG)
                .summary("웹툰 한줄 요약")
                .plot("줄거리")
                .thumbnail("thumbnail.jpg")
                .endFlag(EndFlag.ONGOING)
                .build();

        episode = Episode.builder()
                .idx(1L)
                .epNo(1)
                .title("에피소드 제목")
                .webtoon(webtoon)
                .authorComment("작가의 말")
                .build();
    }

    @DisplayName("별점 등록 성공")
    @Test
    void insertStarRating () {
        //given
        float rating = 5f;
        given(starRatingRepository.existsByEpIdxAndUserIdx(episode.getIdx(), user.getIdx()))
                .willReturn(false);
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(episodeRepository.findById(episode.getIdx())).willReturn(Optional.of(episode));

        //when
        EpisodeStarRatingResponseDto result =
                starRatingService.createStarRating(episode.getIdx(), user.getIdx(), rating);

        //then
        assertAll(
                () -> verify(starRatingRepository).save(any(StarRating.class)),
                () -> verify(episodeRepository).updateRatingAvgAndPersonTotal(episode.getIdx()),
                () -> verify(webtoonRepository).updateRatingAvg(webtoon.getIdx())
        );
    }
}

