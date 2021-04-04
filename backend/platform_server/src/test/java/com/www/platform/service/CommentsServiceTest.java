package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.platform.entity.Comments;
import com.www.core.platform.repository.CommentsDislikeRepository;
import com.www.core.platform.repository.CommentsLikeRepository;
import com.www.core.platform.repository.CommentsRepository;

import org.junit.jupiter.api.*;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {

    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private CommentsLikeRepository commentsLikeRepository;
    @Mock
    private CommentsDislikeRepository commentsDisLikeRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private EpisodeRepository episodeRepository;

    @InjectMocks
    private CommentsService commentsService;

    private static Users user;
    private static Episode validEpisode;
    private static Comments validComment;
    private static String contentWithinSpecifiedLength;

    private final int CONTENT_MAX_LENGTH = 200;

    @BeforeEach
    void beforeEach() {

    }

    @BeforeAll
    static void beforeAll() {
        contentWithinSpecifiedLength = "유효한 길이(200자) 이내의 댓글 내용";

        user = Users.builder()
                .idx(1)
                .id("id123")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();

        validEpisode = Episode.builder()
                .idx(1)
                .ep_no(1)
                .title("에피소드 제목")
                .author_comment("작가의 말")
                .build();

        validComment = Comments.builder()
                .idx(1)
                .users(user)
                .ep(validEpisode)
                .content(contentWithinSpecifiedLength)
                .build();
    }

    @DisplayName("댓글 저장 성공")
    @Order(1)
    @Test
    void insertComments() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(validEpisode.getIdx())).willReturn(Optional.ofNullable(validEpisode));
        given(commentsRepository.save(any(Comments.class))).willReturn(validComment);

        //when
        Response<Integer> result = commentsService.insertComments(user.getIdx(), validEpisode.getIdx(), contentWithinSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentsRepository, times(1)).save(any(Comments.class)),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : insert comment"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 저장 실패 - 에피소드가 존재하지 않음")
    @Order(2)
    @Test
    void insertCommentsFail_EpisodeDoesNotExist() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(2)).willReturn(Optional.empty());

        //when
        Response<Integer> result = commentsService.insertComments(user.getIdx(), 2, contentWithinSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentsRepository, never()).save(any(Comments.class)),
                () -> assertThat(result.getCode()).isEqualTo(20),
                () -> assertThat(result.getMsg()).isEqualTo("fail : episode doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 저장 실패 - 댓글의 길이가 정해진 길이(200자)를 초과")
    @Order(3)
    @Test
    void insertCommentsFail_ContentLengthIsTooLong() {
        //given
        // 한글입숨(http://hangul.thefron.me) 에서 만든 200자 초과 더미 텍스트
        String contentExceedingSpecifiedLength = "꽃이 피가 열매가 봄바람이다. 얼음에 하는 위하여서 오아시스도 보는 그들을 천지는 스며들어 때문이다. 무엇을 되려니와, 우리 이 어디 끓는 품으며, 그들의 너의 것이다. 하는 어디 천자만홍이 살았으며, 그들은 행복스럽고 것이다. 투명하되 있을 불러 가는 옷을 그와 힘차게 산야에 낙원일 것이다. 든 사라지지 트고, 것이다. 있는 실로 품에 어디 이상은 있으랴? 얼마나 같은 것이 있는 그들에게 영락과 가슴에 같이, 이상을 없는 이것이다. 깊이 가지에 이상의 사라지지 못할 얼마나 것이다. 보내는 인생을 따뜻한 우리의 거친 용감하고 힘 있다.";
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(validEpisode.getIdx())).willReturn(Optional.ofNullable(validEpisode));

        //when
        Response<Integer> result = commentsService.insertComments(user.getIdx(), validEpisode.getIdx(), contentExceedingSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentsRepository, never()).save(any(Comments.class)),
                () -> assertThat(contentExceedingSpecifiedLength.length()).isGreaterThan(CONTENT_MAX_LENGTH),
                () -> assertThat(result.getCode()).isEqualTo(27),
                () -> assertThat(result.getMsg()).isEqualTo("fail : content length is too long"),
                () -> assertThat(result.getData()).isNull()
        );
    }


    @DisplayName("댓글 삭제 성공")
    @Order(4)
    @Test
    void deleteComments() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(commentsRepository.findById(validComment.getIdx())).willReturn(Optional.ofNullable(validComment));

        //when
        Response<Integer> result = commentsService.deleteComments(user.getIdx(), validComment.getIdx());

        //then
        assertAll(
                () -> verify(commentsLikeRepository).deleteAllByCommentsIdx(1),
                () -> verify(commentsDisLikeRepository).deleteAllByCommentsIdx(1),
                () -> verify(commentsRepository).deleteById(1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : delete comment"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 삭제 실패 - 유저가 해당 댓글의 작성자가 아닐 때")
    @Order(5)
    @Test
    void deleteCommentsFail_UserIsNotCommenter() {
        //given
        Users otherUser = Users.builder()
                .idx(2)
                .id("userid2")
                .name("짱구")
                .e_pw("123abc")
                .gender(0)
                .email("test2@email.com")
                .build();

        given(usersRepository.findById(otherUser.getIdx())).willReturn(Optional.ofNullable(otherUser));
        given(commentsRepository.findById(validComment.getIdx())).willReturn(Optional.ofNullable(validComment));

        //when
        Response<Integer> result = commentsService.deleteComments(otherUser.getIdx(), validComment.getIdx());

        //then
        assertAll(
                () -> assertThat(otherUser.getIdx()).isNotEqualTo(validComment.getUsers().getIdx()),
                () -> assertThat(result.getCode()).isEqualTo(22),
                () -> assertThat(result.getMsg()).isEqualTo("fail : user isn't commenter"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 삭제 실패 - 삭제하려는 댓글이 없을 때")
    @Order(6)
    @Test
    void deleteCommentsFail_CommentDoesNotExist() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(commentsRepository.findById(validComment.getIdx())).willReturn(Optional.empty());

        //when
        Response<Integer> result = commentsService.deleteComments(user.getIdx(), validComment.getIdx());

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(21),
                () -> assertThat(result.getMsg()).isEqualTo("fail : comment doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    /*@Test
    void getCommentsByPageRequest() {

    }

    @Test
    void getBestComments() {

    }

    @Test
    void getMyPageComments() {

    }*/
}
