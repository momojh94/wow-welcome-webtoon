package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class StarRatingServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private EpisodeRepository episodeRepository;
    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private StarRatingRepository starRatingRepository;

    @InjectMocks
    private StarRatingService starRatingService;

    private Users user;
    private Webtoon webtoon;
    private Episode episode;

    @BeforeEach
    void beforeEach() {
        user = Users.builder()
                .idx(1)
                .id("id1")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();

        webtoon = Webtoon.builder()
                .idx(1)
                .title("웹툰 제목")
                .toon_type(0)
                .genre1(0)
                .genre2(0)
                .summary("웹툰 한줄 요약")
                .plot("줄거리")
                .thumbnail("thumbnail.jpg")
                .end_flag(0)
                .build();

        episode = Episode.builder()
                .idx(1)
                .ep_no(1)
                .title("에피소드 제목")
                .webtoon(webtoon)
                .author_comment("작가의 말")
                .build();
    }

    @DisplayName("별점 등록 성공")
    @Test
    void insertStarRating () {
        //given
        float rating = 8;
        given(starRatingRepository.existsByEpIdxAndUsersIdx(episode.getIdx(), user.getIdx()))
                .willReturn(false);
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
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
        int emptyEpisodeIdx = 22;
        given(starRatingRepository.existsByEpIdxAndUsersIdx(emptyEpisodeIdx, user.getIdx()))
                .willReturn(false);
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
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
        given(starRatingRepository.existsByEpIdxAndUsersIdx(episode.getIdx(), user.getIdx()))
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

