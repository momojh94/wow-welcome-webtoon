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

import com.www.platform.dto.CommentsDto;
import com.www.platform.dto.CommentsResponseDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private CommentsLikeRepository commentsLikeRepository;
    @Mock
    private CommentsDislikeRepository commentsDisLikeRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private EpisodeRepository episodeRepository;

    @InjectMocks
    private CommentsService commentsService;

    private Users user;
    private Episode episode;
    private Comments comment;
    private String contentWithinSpecifiedLength = "제한된 길이(200자) 이내의 댓글 내용";
    private final int CONTENT_MAX_LENGTH = 200;

    @BeforeEach
    void beforeEach() {
        user = Users.builder()
                .idx(1)
                .id("id123")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();

        episode = Episode.builder()
                .idx(1)
                .ep_no(1)
                .title("에피소드 제목")
                .author_comment("작가의 말")
                .build();

        comment = Comments.builder()
                .idx(1)
                .users(user)
                .ep(episode)
                .content(contentWithinSpecifiedLength)
                .build();
    }


    /*@BeforeAll
    static void beforeAll() {

    }*/

    @DisplayName("댓글 저장 성공")
    @Test
    void insertComments() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(episode.getIdx())).willReturn(Optional.ofNullable(episode));
        given(commentsRepository.save(any(Comments.class))).willReturn(comment);

        //when
        Response<Integer> result = commentsService.insertComments(user.getIdx(), episode.getIdx(), contentWithinSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentsRepository, times(1)).save(any(Comments.class)),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : insert comment"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 저장 실패 - 에피소드가 존재하지 않음")
    @Test
    void insertComments_Fail_EpisodeDoesNotExist() {
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
    @Test
    void insertComments_Fail_ContentLengthIsTooLong() {
        //given
        // 한글입숨(http://hangul.thefron.me) 에서 만든 200자 초과 더미 텍스트
        String contentExceedingSpecifiedLength = "꽃이 피가 열매가 봄바람이다. 얼음에 하는 위하여서 오아시스도 보는 그들을 천지는 스며들어 때문이다. 무엇을 되려니와, 우리 이 어디 끓는 품으며, 그들의 너의 것이다. 하는 어디 천자만홍이 살았으며, 그들은 행복스럽고 것이다. 투명하되 있을 불러 가는 옷을 그와 힘차게 산야에 낙원일 것이다. 든 사라지지 트고, 것이다. 있는 실로 품에 어디 이상은 있으랴? 얼마나 같은 것이 있는 그들에게 영락과 가슴에 같이, 이상을 없는 이것이다. 깊이 가지에 이상의 사라지지 못할 얼마나 것이다. 보내는 인생을 따뜻한 우리의 거친 용감하고 힘 있다.";
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(episode.getIdx())).willReturn(Optional.ofNullable(episode));

        //when
        Response<Integer> result = commentsService.insertComments(user.getIdx(), episode.getIdx(), contentExceedingSpecifiedLength);

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
    @Test
    void deleteComments() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(commentsRepository.findById(comment.getIdx())).willReturn(Optional.ofNullable(comment));

        //when
        Response<Integer> result = commentsService.deleteComments(user.getIdx(), comment.getIdx());

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
    @Test
    void deleteComments_Fail_UserIsNotCommenter() {
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
        given(commentsRepository.findById(comment.getIdx())).willReturn(Optional.ofNullable(comment));

        //when
        Response<Integer> result = commentsService.deleteComments(otherUser.getIdx(), comment.getIdx());

        //then
        assertAll(
                () -> assertThat(otherUser.getIdx()).isNotEqualTo(comment.getUsers().getIdx()),
                () -> assertThat(result.getCode()).isEqualTo(22),
                () -> assertThat(result.getMsg()).isEqualTo("fail : user isn't commenter"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 삭제 실패 - 삭제하려는 댓글이 없을 때")
    @Test
    void deleteComments_Fail_CommentDoesNotExist() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(commentsRepository.findById(comment.getIdx())).willReturn(Optional.empty());

        //when
        Response<Integer> result = commentsService.deleteComments(user.getIdx(), comment.getIdx());

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(21),
                () -> assertThat(result.getMsg()).isEqualTo("fail : comment doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 조회 성공 - 총 3개의 페이지 중 2번 페이지 조회" )
    @Test
    void getCommentsByPageRequest() {
        //given
        int page = 2;
        List<Comments> commentsList = new ArrayList<>();
        for (int idx = 1; idx <= 33; idx++) {
            commentsList.add(Comments.builder()
                    .idx(idx)
                    .users(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .build());
        }
        int fromIndex = CommentsService.COMMENTS_COUNT_PER_PAGE * (page - 1);
        int toIndex = fromIndex + CommentsService.COMMENTS_COUNT_PER_PAGE;
        List<Comments> subList = commentsList.subList(fromIndex, toIndex);
        Pageable pageable = PageRequest.of(page - 1, CommentsService.COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comments> commentsPage = new PageImpl<Comments>(subList, pageable, commentsList.size());

        given(episodeRepository.existsById(episode.getIdx())).willReturn(true);
        given(commentsRepository.findAllByEpIdx(pageable, episode.getIdx())).willReturn(commentsPage);

        //when
        Response<CommentsResponseDto> result = commentsService.getCommentsByPageRequest(episode.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(fromIndex),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentsService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : get comments by page request"),
                () -> assertThat(result.getData().getComments().size()).isEqualTo(CommentsService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getData().getTotal_pages()).isEqualTo(commentsPage.getTotalPages())
        );
    }

    @DisplayName("댓글 조회 실패 - 존재하지 않은 에피소드 접근")
    @Test
    void getCommentsByPageRequest_Fail_EpisodeDoesNotExist() {
        //given
        int page = 1;
        int epIdx = 2;
        given(episodeRepository.existsById(2)).willReturn(false);

        //when
        Response<CommentsResponseDto> result = commentsService.getCommentsByPageRequest(epIdx, page);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(20),
                () -> assertThat(result.getMsg()).isEqualTo("fail : episode doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 조회 실패 - 잘못된 page값(1보다 작을 때)")
    @Test
    void getCommentsByPageRequest_Fail_InvalidPageValue() {
        //given
        int page = -3;
        given(episodeRepository.existsById(episode.getIdx())).willReturn(true);

        //when
        Response<CommentsResponseDto> result = commentsService.getCommentsByPageRequest(episode.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(23),
                () -> assertThat(result.getMsg()).isEqualTo("fail : invalid page number"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 조회 실패 - 잘못된 page값(1이 아니며, 최대 페이지 수보다 클 때)")
    @Test
    void getCommentsByPageRequest_Fail_InvalidPageValue2() {
        //given
        int page = 7;
        List<Comments> commentsList = new ArrayList<>();
        for (int idx = 1; idx <= 33; idx++) {
            commentsList.add(Comments.builder()
                    .idx(idx)
                    .users(user)
                    .ep(episode)
                    .content("댓글 내용 ~~~ " + idx)
                    .build());
        }
        List<Comments> subList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, CommentsService.COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comments> commentsPage = new PageImpl<Comments>(subList, pageable, commentsList.size());

        given(episodeRepository.existsById(episode.getIdx())).willReturn(true);
        given(commentsRepository.findAllByEpIdx(pageable, episode.getIdx())).willReturn(commentsPage);

        //when
        Response<CommentsResponseDto> result = commentsService.getCommentsByPageRequest(episode.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(CommentsService.COMMENTS_COUNT_PER_PAGE * (page - 1)),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentsService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getCode()).isEqualTo(23),
                () -> assertThat(result.getMsg()).isEqualTo("fail : invalid page number"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    /*
    @Test
    void getBestComments() {

    }

    @Test
    void getMyPageComments() {

    }*/
}
