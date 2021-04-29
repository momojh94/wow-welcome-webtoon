package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;
import com.www.core.common.Response;
import com.www.core.platform.entity.Comments;
import com.www.core.platform.entity.CommentsDislike;
import com.www.core.platform.entity.CommentsLike;
import com.www.core.platform.repository.CommentsDislikeRepository;
import com.www.core.platform.repository.CommentsLikeRepository;
import com.www.core.platform.repository.CommentsRepository;
import com.www.platform.dto.CommentsLikeDislikeCntResponseDto;
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
public class CommentsLikeDislikeServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private CommentsLikeRepository commentsLikeRepository;
    @Mock
    private CommentsDislikeRepository commentsDislikeRepository;

    @InjectMocks
    private CommentsLikeDislikeService commentsLikeDislikeService;

    private Users user;
    private Users otherUser;
    private Comments commentByUser;
    private Comments commentByOtherUser;

    @BeforeEach
    void beforeEach() {
        user = Users.builder()
                .idx(1L)
                .account("id1")
                .name("철수")
                .pw("1q2w3e4r")
                .gender((byte) 0)
                .email("test@email.com")
                .build();

        otherUser = Users.builder()
                .idx(2L)
                .account("id2")
                .name("영희")
                .pw("1q2w3e")
                .gender((byte) 0)
                .email("test2@email.com")
                .build();

        commentByUser = Comments.builder()
                .idx(1L)
                .user(user)
                .likeCount(5)
                .dislikeCount(3)
                .content("댓글 내용")
                .build();

        commentByOtherUser = Comments.builder()
                .idx(2L)
                .user(otherUser)
                .likeCount(13)
                .dislikeCount(7)
                .content("댓글 내용2")
                .build();
    }

    @DisplayName("좋아요 요청 성공 - 좋아요 요청이 성공하여 좋아요 수 증가")
    @Test
    void requestLike_success() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByOtherUser.getIdx())).willReturn(Optional.of(commentByOtherUser));
        given(commentsLikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(false);

        //when
        Response<CommentsLikeDislikeCntResponseDto> result = commentsLikeDislikeService.requestLike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentsLikeRepository).save(any(CommentsLike.class)),
                () -> verify(commentsRepository).updateLikeCount(commentByOtherUser.getIdx(), 1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : success request like"),
                () -> assertThat(result.getData().getCnt()).isEqualTo(commentByOtherUser.getLikeCount() + 1)
        );
    }

    @DisplayName("좋아요 요청 성공 - 좋아요 취소 요청이 성공하여 기존의 좋아요 취소, 좋아요 수 감소")
    @Test
    void requestLike_cancle() {
        //given
        CommentsLike commentsLike = CommentsLike.builder()
                .idx(1L)
                .user(user)
                .comment(commentByOtherUser)
                .build();
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByOtherUser.getIdx())).willReturn(Optional.of(commentByOtherUser));
        given(commentsLikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(true);
        given(commentsLikeRepository.findByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(commentsLike);

        //when
        Response<CommentsLikeDislikeCntResponseDto> result = commentsLikeDislikeService.requestLike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentsLikeRepository).deleteByIdx(commentsLike.getIdx()),
                () -> verify(commentsRepository).updateLikeCount(commentByOtherUser.getIdx(), -1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : cancel request like"),
                () -> assertThat(result.getData().getCnt()).isEqualTo(commentByOtherUser.getLikeCount() - 1)
        );
    }

    @DisplayName("좋아요 요청 실패 - 존재하지 않은 댓글 접근")
    @Test
    void requestLike_fail_commentDoesNotExist() {
        //given
        Long emptyCommentIdx = 5L;
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(emptyCommentIdx)).willReturn(Optional.empty());

        //when
        Response<CommentsLikeDislikeCntResponseDto> result = commentsLikeDislikeService.requestLike(user.getIdx(), emptyCommentIdx);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(21),
                () -> assertThat(result.getMsg()).isEqualTo("fail : comment doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("좋아요 요청 실패 - 유저 자신이 쓴 댓글에 좋아요 요청 할 때")
    @Test
    void requestLike_fail_userIsLikeCommenter() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByUser.getIdx())).willReturn(Optional.of(commentByUser));

        //when
        Response<CommentsLikeDislikeCntResponseDto> result = commentsLikeDislikeService.requestLike(user.getIdx(), commentByUser.getIdx());

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(24),
                () -> assertThat(result.getMsg()).isEqualTo("fail : users can't request like on their own comments"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("싫어요 요청 성공 - 싫어요 요청이 성공하여 싫어요 수 증가")
    @Test
    void requestDislike_success() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByOtherUser.getIdx())).willReturn(Optional.of(commentByOtherUser));
        given(commentsDislikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(false);

        //when
        Response<CommentsLikeDislikeCntResponseDto> result =
                commentsLikeDislikeService.requestDislike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentsDislikeRepository).save(any(CommentsDislike.class)),
                () -> verify(commentsRepository).updateDislikeCount(commentByOtherUser.getIdx(), 1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : success request dislike"),
                () -> assertThat(result.getData().getCnt()).isEqualTo(commentByOtherUser.getDislikeCount() + 1)
        );
    }

    @DisplayName("싫어요 요청 성공 - 싫어요 취소 요청이 성공하여 기존의 싫어요 취소, 싫어요 수 감소")
    @Test
    void requestDislike_cancle() {
        //given
        CommentsDislike commentsDislike = CommentsDislike.builder()
                .idx(1L)
                .user(user)
                .comment(commentByOtherUser)
                .build();
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByOtherUser.getIdx())).willReturn(Optional.of(commentByOtherUser));
        given(commentsDislikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(true);
        given(commentsDislikeRepository.findByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(commentsDislike);

        //when
        Response<CommentsLikeDislikeCntResponseDto> result =
                commentsLikeDislikeService.requestDislike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentsDislikeRepository).deleteByIdx(commentsDislike.getIdx()),
                () -> verify(commentsRepository).updateDislikeCount(commentByOtherUser.getIdx(), -1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : cancel request dislike"),
                () -> assertThat(result.getData().getCnt()).isEqualTo(commentByOtherUser.getDislikeCount() - 1)
        );
    }

    @DisplayName("싫어요 요청 실패 - 존재하지 않은 댓글 접근")
    @Test
    void requestDislike_fail_commentDoesNotExist() {
        //given
        Long emptyCommentIdx = 13L;
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(emptyCommentIdx)).willReturn(Optional.empty());

        //when
        Response<CommentsLikeDislikeCntResponseDto> result =
                commentsLikeDislikeService.requestDislike(user.getIdx(), emptyCommentIdx);

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(21),
                () -> assertThat(result.getMsg()).isEqualTo("fail : comment doesn't exist"),
                () -> assertThat(result.getData()).isNull()
        );
    }

    @DisplayName("싫어요 요청 실패 - 유저 자신이 쓴 댓글에 싫어요 요청 할 때")
    @Test
    void requestDislike_fail_userIsLikeCommenter() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByUser.getIdx())).willReturn(Optional.of(commentByUser));

        //when
        Response<CommentsLikeDislikeCntResponseDto> result =
                commentsLikeDislikeService.requestDislike(user.getIdx(), commentByUser.getIdx());

        //then
        assertAll(
                () -> assertThat(result.getCode()).isEqualTo(25),
                () -> assertThat(result.getMsg()).isEqualTo("fail : users can't request dislike on their own comments"),
                () -> assertThat(result.getData()).isNull()
        );
    }
}
