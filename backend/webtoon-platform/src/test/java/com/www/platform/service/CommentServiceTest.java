package com.www.platform.service;

import com.www.core.auth.Gender;
import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.platform.entity.Comment;
import com.www.core.platform.repository.CommentDislikeRepository;
import com.www.core.platform.repository.CommentLikeRepository;
import com.www.core.platform.repository.CommentRepository;
import com.www.platform.dto.CommentDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.dto.MyPageCommentDto;
import com.www.platform.dto.MyPageCommentsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.BDDMockito.*;


/*
@Mock
mock 객체 생성

@injectMocks
@Mock이 붙은 mock객체를 @injectMocks이 붙은 객체에 주입시킬 수 있다.
 */

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentDislikeRepository commentDisLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EpisodeRepository episodeRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Webtoon webtoon;
    private Episode episode;
    private Comment comment;
    private String contentWithinSpecifiedLength = "제한된 길이(200자) 이내의 댓글 내용";
    private final int CONTENT_MAX_LENGTH = 200;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .idx(1L)
                .account("id123")
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

        comment = Comment.builder()
                .idx(1L)
                .user(user)
                .ep(episode)
                .content(contentWithinSpecifiedLength)
                .build();
    }


    /*@BeforeAll
    static void beforeAll() {

    }*/

    @DisplayName("댓글 저장 성공")
    @Test
    void insertComment() {
        //given
        given(userRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(episode.getIdx())).willReturn(Optional.ofNullable(episode));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        Response<Long> result = commentService.insertComment(user.getIdx(), episode.getIdx(), contentWithinSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentRepository, times(1)).save(any(Comment.class)),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : insert comment"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 저장 실패 - 에피소드가 존재하지 않음")
    @Test
    void insertComment_Fail_EpisodeDoesNotExist() {
        //given
        given(userRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(2L)).willReturn(Optional.empty());

        //when
        Response<Long> result = commentService.insertComment(user.getIdx(), 2L, contentWithinSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentRepository, never()).save(any(Comment.class)),
                () -> assertThat(result.getCode()).isEqualTo(20),
                () -> assertThat(result.getMsg()).isEqualTo("fail : episode doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 저장 실패 - 댓글의 길이가 정해진 길이(200자)를 초과")
    @Test
    void insertComment_Fail_ContentLengthIsTooLong() {
        //given
        // 한글입숨(http://hangul.thefron.me) 에서 만든 200자 초과 더미 텍스트
        String contentExceedingSpecifiedLength = "꽃이 피가 열매가 봄바람이다. 얼음에 하는 위하여서 오아시스도 보는 그들을 천지는 스며들어 때문이다. 무엇을 되려니와, 우리 이 어디 끓는 품으며, 그들의 너의 것이다. 하는 어디 천자만홍이 살았으며, 그들은 행복스럽고 것이다. 투명하되 있을 불러 가는 옷을 그와 힘차게 산야에 낙원일 것이다. 든 사라지지 트고, 것이다. 있는 실로 품에 어디 이상은 있으랴? 얼마나 같은 것이 있는 그들에게 영락과 가슴에 같이, 이상을 없는 이것이다. 깊이 가지에 이상의 사라지지 못할 얼마나 것이다. 보내는 인생을 따뜻한 우리의 거친 용감하고 힘 있다.";
        given(userRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(episodeRepository.findById(episode.getIdx())).willReturn(Optional.ofNullable(episode));

        //when
        Response<Long> result = commentService.insertComment(user.getIdx(), episode.getIdx(), contentExceedingSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentRepository, never()).save(any(Comment.class)),
                () -> assertThat(contentExceedingSpecifiedLength.length()).isGreaterThan(CONTENT_MAX_LENGTH),
                () -> assertThat(result.getCode()).isEqualTo(27),
                () -> assertThat(result.getMsg()).isEqualTo("fail : content length is too long"),
                () -> assertThat(result.getData()).isNull()
        );
    }


    @DisplayName("댓글 삭제 성공")
    @Test
    void deleteComment() {
        //given
        given(userRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(commentRepository.findById(comment.getIdx())).willReturn(Optional.ofNullable(comment));

        //when
        Response<Long> result = commentService.deleteComment(user.getIdx(), comment.getIdx());

        //then
        assertAll(
                () -> verify(commentLikeRepository).deleteAllByCommentIdx(1L),
                () -> verify(commentDisLikeRepository).deleteAllBycommentIdx(1L),
                () -> verify(commentRepository).deleteById(1L),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : delete comment"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 삭제 실패 - 유저가 해당 댓글의 작성자가 아닐 때")
    @Test
    void deleteComment_Fail_UserIsNotCommenter() {
        //given
        User otherUser = User.builder()
                .idx(2L)
                .account("userid2")
                .name("짱구")
                .pw("123abc")
                .gender(Gender.MALE)
                .email("test2@email.com")
                .build();

        given(userRepository.findById(otherUser.getIdx())).willReturn(Optional.of(otherUser));
        given(commentRepository.findById(comment.getIdx())).willReturn(Optional.ofNullable(comment));

        //when
        Response<Long> result = commentService.deleteComment(otherUser.getIdx(), comment.getIdx());

        //then
        assertAll(
                () -> assertThat(otherUser.getIdx()).isNotEqualTo(comment.getUser().getIdx()),
                () -> assertThat(result.getCode()).isEqualTo(22),
                () -> assertThat(result.getMsg()).isEqualTo("fail : user isn't commenter"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("댓글 삭제 실패 - 삭제하려는 댓글이 없을 때")
    @Test
    void deleteComment_Fail_CommentDoesNotExist() {
        //given
        given(userRepository.findById(user.getIdx())).willReturn(Optional.ofNullable(user));
        given(commentRepository.findById(comment.getIdx())).willReturn(Optional.empty());

        //when
        Response<Long> result = commentService.deleteComment(user.getIdx(), comment.getIdx());

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
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 1L; idx <= 33L; idx++) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .build());
        }
        int fromIndex = CommentService.COMMENTS_COUNT_PER_PAGE * (page - 1);
        int toIndex = fromIndex + CommentService.COMMENTS_COUNT_PER_PAGE;
        List<Comment> subList = commentList.subList(fromIndex, toIndex);
        Pageable pageable = PageRequest.of(page - 1, CommentService.COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = new PageImpl<>(subList, pageable, commentList.size());

        given(episodeRepository.existsById(episode.getIdx())).willReturn(true);
        given(commentRepository.findAllByEpIdx(pageable, episode.getIdx())).willReturn(commentsPage);

        //when
        Response<CommentsResponseDto> result = commentService.getCommentsByPageRequest(episode.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(fromIndex),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : get comments by page request"),
                () -> assertThat(result.getData().getComments().size()).isEqualTo(CommentService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getData().getTotalPages()).isEqualTo(commentsPage.getTotalPages())
        );
    }

    @DisplayName("댓글 조회 실패 - 존재하지 않은 에피소드 접근")
    @Test
    void getCommentsByPageRequest_Fail_EpisodeDoesNotExist() {
        //given
        int page = 1;
        Long epIdx = 2L;
        given(episodeRepository.existsById(2L)).willReturn(false);

        //when
        Response<CommentsResponseDto> result = commentService.getCommentsByPageRequest(epIdx, page);

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
        Response<CommentsResponseDto> result = commentService.getCommentsByPageRequest(episode.getIdx(), page);

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
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 1L; idx <= 33L; idx++) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 ~~~ " + idx)
                    .build());
        }
        List<Comment> subList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, CommentService.COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = new PageImpl<Comment>(subList, pageable, commentList.size());

        given(episodeRepository.existsById(episode.getIdx())).willReturn(true);
        given(commentRepository.findAllByEpIdx(pageable, episode.getIdx())).willReturn(commentsPage);

        //when
        Response<CommentsResponseDto> result = commentService.getCommentsByPageRequest(episode.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(CommentService.COMMENTS_COUNT_PER_PAGE * (page - 1)),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getCode()).isEqualTo(23),
                () -> assertThat(result.getMsg()).isEqualTo("fail : invalid page number"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("베스트 댓글 조회 성공")
    @Test
    void getBestComments() {
        //given
        Long epIdx = 1L;
        List<Comment> bestCommentList = new ArrayList<Comment>();
        for (Long idx = 0L; idx < 5; idx++) {
            bestCommentList.add(Comment.builder()
                    .idx(idx+22)
                    .user(user)
                    .ep(episode)
                    .content("베스트 댓글 내용 " + idx)
                    .build());
        }
        given(episodeRepository.existsById(epIdx)).willReturn(true);
        given(commentRepository.findBestCommentsByEpIdx(epIdx)).willReturn(bestCommentList.stream());

        //when
        Response<List<CommentDto>> result = commentService.getBestComments(epIdx);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("requset complete : get best comments"),
                () -> assertArrayEquals(bestCommentList.stream()
                        .map(Comment::getIdx)
                        .toArray(Long[]::new),
                        result.getData().stream()
                        .map(CommentDto::getIdx)
                        .toArray(Long[]::new))
        );
    }

    @DisplayName("베스트 댓글 조회 실패 - 존재하지 않은 에피소드 접근")
    @Test
    void getBestComments_Fail_CommentDoesNotExist() {
        //given
        Long epIdx = 2L;
        given(episodeRepository.existsById(epIdx)).willReturn(false);

        //when
        Response<List<CommentDto>> result = commentService.getBestComments(epIdx);

        //then
        assertAll(
                () -> verify(commentRepository, never()).findBestCommentsByEpIdx(epIdx),
                () -> assertThat(result.getCode()).isEqualTo(20),
                () -> assertThat(result.getMsg()).isEqualTo("fail : episode doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("마이 페이지 내가 쓴 댓글 조회 성공")
    @Test
    void getMyPageComments() {
        //given
        int page = 2;
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 1L; idx <= 17L; idx++) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .build());
        }
        int fromIndex = CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE * (page - 1);
        int toIndex = Math.min(fromIndex + CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE, commentList.size());
        List<Comment> subList = commentList.subList(fromIndex, toIndex);
        Pageable pageable = PageRequest.of(page - 1, CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = new PageImpl<Comment>(subList, pageable, commentList.size());

        given(commentRepository.findAllByUserIdx(pageable, user.getIdx())).willReturn(commentsPage);

        //when
        Response<MyPageCommentsResponseDto> result = commentService.getMyPageComments(user.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(fromIndex),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : get my page comments"),
                () -> assertThat(result.getData().getComments().size()).isEqualTo(commentsPage.getNumberOfElements()),
                () -> assertThat(result.getData().getTotalPages()).isEqualTo(commentsPage.getTotalPages()),
                () -> assertArrayEquals(subList.stream()
                                .map(Comment::getIdx)
                                .toArray(Long[]::new),
                        result.getData().getComments().stream()
                                .map(MyPageCommentDto::getIdx)
                                .toArray(Long[]::new))
        );
    }

    @DisplayName("마이 페이지 내가 쓴 댓글 조회 실패 - 잘못된 page 값(1보다 작을 때)")
    @Test
    void getMyPageComments_Fail_InvalidPageValue() {
        //given
        int page = -3;
        //when
        Response<MyPageCommentsResponseDto> result = commentService.getMyPageComments(user.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(23),
                () -> assertThat(result.getMsg()).isEqualTo("fail : invalid page number"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("마이 페이지 내가 쓴 댓글 조회 실패 - 잘못된 page값(1이 아니며, 최대 페이지 수보다 클 때)")
    @Test
    void getMyPageComments_Fail_InvalidPageValue2() {
        //given
        int page = 10;
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 1L; idx <= 33L; idx++) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 ~~~ " + idx)
                    .build());
        }
        List<Comment> emptyList = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = new PageImpl<Comment>(emptyList, pageable, commentList.size());

        given(commentRepository.findAllByUserIdx(pageable, user.getIdx())).willReturn(commentsPage);

        //when
        Response<MyPageCommentsResponseDto> result = commentService.getMyPageComments(user.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE * (page - 1)),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getCode()).isEqualTo(23),
                () -> assertThat(result.getMsg()).isEqualTo("fail : invalid page number"),
                () -> assertThat(result.getData()).isNull()
        );
    }
}
