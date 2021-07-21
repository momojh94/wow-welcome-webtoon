package com.webtoon.api.comment.service;

import com.webtoon.core.auth.entity.User;
import com.webtoon.core.auth.enums.Gender;
import com.webtoon.core.auth.repository.UserRepository;
import com.webtoon.core.platform.entity.Comment;
import com.webtoon.core.platform.entity.CommentDislike;
import com.webtoon.core.platform.entity.CommentLike;
import com.webtoon.core.platform.repository.CommentDislikeRepository;
import com.webtoon.core.platform.repository.CommentLikeRepository;
import com.webtoon.core.platform.repository.CommentRepository;
import com.webtoon.api.comment.dto.CommentLikeDislikeCountResponseDto;
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
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentLikeDislikeServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentDislikeRepository commentDislikeRepository;

    @InjectMocks
    private CommentLikeDislikeService commentLikeDislikeService;

    private User user;
    private User otherUser;
    private Comment commentByUser;
    private Comment commentByOtherUser;

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

        otherUser = User.builder()
                .idx(2L)
                .account("id2")
                .name("영희")
                .pw("1q2w3e")
                .gender(Gender.MALE)
                .email("test2@email.com")
                .build();

        commentByUser = Comment.builder()
                .idx(1L)
                .user(user)
                .likeCount(5)
                .dislikeCount(3)
                .content("댓글 내용")
                .build();

        commentByOtherUser = Comment.builder()
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
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentRepository.findById(commentByOtherUser.getIdx()))
                .willReturn(Optional.of(commentByOtherUser));
        given(commentLikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(false);

        //when
        CommentLikeDislikeCountResponseDto result
                = commentLikeDislikeService.requestLike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentLikeRepository).save(any(CommentLike.class)),
                () -> verify(commentRepository).updateLikeCount(commentByOtherUser.getIdx(), 1),
                () -> assertThat(result.getCount()).isEqualTo(commentByOtherUser.getLikeCount() + 1)
        );
    }

    @DisplayName("좋아요 요청 성공 - 좋아요 취소 요청이 성공하여 기존의 좋아요 취소, 좋아요 수 감소")
    @Test
    void requestLike_cancle() {
        //given
        CommentLike commentLike = CommentLike.builder()
                .idx(1L)
                .user(user)
                .comment(commentByOtherUser)
                .build();
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentRepository.findById(commentByOtherUser.getIdx()))
                .willReturn(Optional.of(commentByOtherUser));
        given(commentLikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(true);
        given(commentLikeRepository.findByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(commentLike);

        //when
        CommentLikeDislikeCountResponseDto result =
                commentLikeDislikeService.requestLike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentLikeRepository).deleteByIdx(commentLike.getIdx()),
                () -> verify(commentRepository).updateLikeCount(commentByOtherUser.getIdx(), -1),
                () -> assertThat(result.getCount()).isEqualTo(commentByOtherUser.getLikeCount() - 1)
        );
    }

    @DisplayName("싫어요 요청 성공 - 싫어요 요청이 성공하여 싫어요 수 증가")
    @Test
    void requestDislike_success() {
        //given
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentRepository.findById(commentByOtherUser.getIdx()))
                .willReturn(Optional.of(commentByOtherUser));
        given(commentDislikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(false);

        //when
        CommentLikeDislikeCountResponseDto result =
                commentLikeDislikeService.requestDislike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentDislikeRepository).save(any(CommentDislike.class)),
                () -> verify(commentRepository).updateDislikeCount(commentByOtherUser.getIdx(), 1),
                () -> assertThat(result.getCount()).isEqualTo(commentByOtherUser.getDislikeCount() + 1)
        );
    }

    @DisplayName("싫어요 요청 성공 - 싫어요 취소 요청이 성공하여 기존의 싫어요 취소, 싫어요 수 감소")
    @Test
    void requestDislike_cancle() {
        //given
        CommentDislike commentDislike = CommentDislike.builder()
                .idx(1L)
                .user(user)
                .comment(commentByOtherUser)
                .build();
        given(userRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentRepository.findById(commentByOtherUser.getIdx()))
                .willReturn(Optional.of(commentByOtherUser));
        given(commentDislikeRepository.existsByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(true);
        given(commentDislikeRepository.findByCommentIdxAndUserIdx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(commentDislike);

        //when
        CommentLikeDislikeCountResponseDto result =
                commentLikeDislikeService.requestDislike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentDislikeRepository).deleteByIdx(commentDislike.getIdx()),
                () -> verify(commentRepository).updateDislikeCount(commentByOtherUser.getIdx(), -1),
                () -> assertThat(result.getCount()).isEqualTo(commentByOtherUser.getDislikeCount() - 1)
        );
    }
}
