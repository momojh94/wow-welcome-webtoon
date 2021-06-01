package com.www.platform.service;

import com.www.core.auth.enums.Gender;
import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.file.repository.WebtoonRepository;
import com.www.core.platform.entity.StarRating;
import com.www.core.platform.repository.StarRatingRepository;
import com.www.platform.dto.EpisodeStarRatingResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

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
                .toonType((byte) 0)
                .genre1((byte) 0)
                .genre2((byte) 0)
                .summary("웹툰 한줄 요약")
                .plot("줄거리")
                .thumbnail("thumbnail.jpg")
                .endFlag((byte) 0)
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
        float rating = 8;
        given(starRatingRepository.existsByEpIdxAndUserIdx(episode.getIdx(), user.getIdx()))
                .willReturn(false);
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(episodeRepository.findById(episode.getIdx())).willReturn(Optional.of(episode));

        //when
        Response<EpisodeStarRatingResponseDto> result =
                starRatingService.insertStarRating(episode.getIdx(), user.getIdx(), rating);

        //then
        assertAll(
                () -> verify(starRatingRepository).save(any(StarRating.class)),
                () -> verify(episodeRepository).updateRatingAvgAndPersonTotal(episode.getIdx()),
                () -> verify(webtoonRepository).updateRatingAvg(webtoon.getIdx()),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : insert star rating"),
                () -> assertThat(result.getData()).isInstanceOf(EpisodeStarRatingResponseDto.class)
        );
    }

    @DisplayName("별점 등록 실패 - 존재하지 않은 episode 접근")
    @Test
    void insertStarRating_fail_episodeDoesNotExist() {
        //given
        float rating = 7;
        Long emptyEpisodeIdx = 22L;
        given(starRatingRepository.existsByEpIdxAndUserIdx(emptyEpisodeIdx, user.getIdx()))
                .willReturn(false);
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(episodeRepository.findById(emptyEpisodeIdx)).willReturn(Optional.empty());

        //when
        Response<EpisodeStarRatingResponseDto> result =
                starRatingService.insertStarRating(emptyEpisodeIdx, user.getIdx(), rating);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(20),
                () -> assertThat(result.getMsg()).isEqualTo("fail : episode doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("별점 등록 실패 - 이미 해당 episode에 별점을 준 적이 있는 경우")
    @Test
    void insertStarRating_fail_HaveAlreadyGivenStarRating() {
        //given
        float rating = 5;
        given(starRatingRepository.existsByEpIdxAndUserIdx(episode.getIdx(), user.getIdx()))
                .willReturn(true);

        //when
        Response<EpisodeStarRatingResponseDto> result =
                starRatingService.insertStarRating(episode.getIdx(), user.getIdx(), rating);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(26),
                () -> assertThat(result.getMsg()).isEqualTo("fail : have already given star rating"),
                () -> assertThat(result.getData()).isNull()
        );
    }
}

