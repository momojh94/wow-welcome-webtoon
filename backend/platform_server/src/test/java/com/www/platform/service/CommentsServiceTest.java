package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.platform.entity.Comments;
import com.www.core.platform.repository.CommentsRepository;

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


/*
@Mock
mock 객체 생성

@injectMocks
@Mock이 붙은 mock객체를 @injectMocks이 붙은 객체에 주입시킬 수 있다.
 */

@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {

    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private EpisodeRepository episodeRepository;

    @InjectMocks
    private CommentsService commentsService;

    private Users user;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id("id123")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();
    }

    @DisplayName("댓글 저장 성공")
    @Test
    void insertComments() {
        //given
        String content = "댓글 내용";
        Episode episode = Episode.builder()
                .ep_no(1)
                .title("에피소드 이름")
                .author_comment("작가의 말")
                .build();
        Comments comment = Comments.builder()
                .users(user)
                .ep(episode)
                .content(content)
                .build();
        given(usersRepository.findById(1)).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(1)).willReturn(Optional.ofNullable(episode));
        given(commentsRepository.save(any(Comments.class))).willReturn(comment);

        //when
        Response<Integer> result = commentsService.insertComments(1, 1, content);

        //then
        verify(commentsRepository, times(1)).save(any(Comments.class));
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : insert comment"),
                () -> assertThat(result.getData()).isEqualTo(null)
        );
    }

    @DisplayName("댓글 저장 실패, 에피소드가 존재하지 않음")
    @Test
    void insertCommentsFail_EpisodeDoesNotExist() {
        //given
        String content = "댓글 내용";
        given(usersRepository.findById(1)).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(1)).willReturn(Optional.empty());

        //when
        Response<Integer> result = commentsService.insertComments(1, 1, content);

        //then
        verify(commentsRepository, times(0)).save(any(Comments.class));
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(20),
                () -> assertThat(result.getMsg()).isEqualTo("fail : episode doesn't exist"),
                () -> assertThat(result.getData()).isEqualTo(null)
        );
    }

    @DisplayName("댓글 저장 실패, 댓글의 길이가 정해진 길이(200자)를 초과")
    @Test
    void insertCommentsFail_ContentLengthIsTooLong() {
        //given
        // 한글입숨(http://hangul.thefron.me) 에서 만든 200자 초과 더미 텍스트
        String content = "꽃이 피가 열매가 봄바람이다. 얼음에 하는 위하여서 오아시스도 보는 그들을 천지는 스며들어 때문이다. 무엇을 되려니와, 우리 이 어디 끓는 품으며, 그들의 너의 것이다. 하는 어디 천자만홍이 살았으며, 그들은 행복스럽고 것이다. 투명하되 있을 불러 가는 옷을 그와 힘차게 산야에 낙원일 것이다. 든 사라지지 트고, 것이다. 있는 실로 품에 어디 이상은 있으랴? 얼마나 같은 것이 있는 그들에게 영락과 가슴에 같이, 이상을 없는 이것이다. 깊이 가지에 이상의 사라지지 못할 얼마나 것이다. 보내는 인생을 따뜻한 우리의 거친 용감하고 힘 있다.";
        Episode episode = Episode.builder()
                .ep_no(1)
                .title("에피소드 이름")
                .author_comment("작가의 말")
                .build();
        given(usersRepository.findById(1)).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(1)).willReturn(Optional.ofNullable(episode));

        //when
        Response<Integer> result = commentsService.insertComments(1, 1, content);

        //then
        verify(commentsRepository, times(0)).save(any(Comments.class));
        assertAll(
                () -> assertThat(content.length()).isGreaterThan(200),
                () -> assertThat(result.getCode()).isEqualTo(27),
                () -> assertThat(result.getMsg()).isEqualTo("fail : content length is too long"),
                () -> assertThat(result.getData()).isEqualTo(null)
        );
    }


    /*@Test
    void deleteComments() {

    }

    @Test
    void getCommentsByPageRequest() {

    }

    @Test
    void getBestComments() {

    }

    @Test
    void getMyPageComments() {

    }*/
}
