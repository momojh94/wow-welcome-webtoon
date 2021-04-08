package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;
import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.platform.entity.Comments;
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
                .idx(1)
                .id("id1")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();

        otherUser = Users.builder()
                .idx(2)
                .id("id2")
                .name("영희")
                .e_pw("1q2w3e")
                .gender(0)
                .email("test2@email.com")
                .build();

        commentByUser = Comments.builder()
                .idx(1)
                .users(user)
                .like_cnt(5)
                .dislike_cnt(3)
                .content("댓글 내용")
                .build();

        commentByOtherUser = Comments.builder()
                .idx(2)
                .users(otherUser)
                .like_cnt(3)
                .dislike_cnt(2)
                .content("댓글 내용2")
                .build();
    }

    @DisplayName("좋아요 요청 성공 - 요청이 성공하여 좋아요 수 증가")
    @Test
    void requestLike_success() {
        //given
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByOtherUser.getIdx())).willReturn(Optional.of(commentByOtherUser));
        given(commentsLikeRepository.existsByComments_IdxAndUsers_Idx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(false);

        //when
        Response<CommentsLikeDislikeCntResponseDto> result = commentsLikeDislikeService.requestLike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentsLikeRepository).save(any(CommentsLike.class)),
                () -> verify(commentsRepository).updateLikeCnt(commentByOtherUser.getIdx(), 1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : success request like"),
                () -> assertThat(result.getData().getCnt()).isEqualTo(commentByOtherUser.getLike_cnt() + 1)
        );
    }

    @DisplayName("좋아요 요청 성공 - 취소 요청이 성공하여 기존의 좋아요 취소, 좋아요 수 감소")
    @Test
    void requestLike_cancle() {
        //given
        CommentsLike commentsLike = CommentsLike.builder()
                .idx(1)
                .users_idx(user)
                .comments_idx(commentByOtherUser)
                .build();
        given(usersRepository.findById(user.getIdx())).willReturn(Optional.of(user));
        given(commentsRepository.findById(commentByOtherUser.getIdx())).willReturn(Optional.of(commentByOtherUser));
        given(commentsLikeRepository.existsByComments_IdxAndUsers_Idx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(true);
        given(commentsLikeRepository.findByComments_IdxAndUsers_Idx(commentByOtherUser.getIdx(), user.getIdx()))
                .willReturn(commentsLike);

        //when
        Response<CommentsLikeDislikeCntResponseDto> result = commentsLikeDislikeService.requestLike(user.getIdx(), commentByOtherUser.getIdx());

        //then
        assertAll(
                () -> verify(commentsLikeRepository).deleteByIdx(commentsLike.getIdx()),
                () -> verify(commentsRepository).updateLikeCnt(commentByOtherUser.getIdx(), -1),
                () -> assertThat(result.getCode()).isEqualTo(0),
                () -> assertThat(result.getMsg()).isEqualTo("request complete : cancel request like"),
                () -> assertThat(result.getData().getCnt()).isEqualTo(commentByOtherUser.getLike_cnt() - 1)
        );
    }

    @DisplayName("좋아요 요청 실패 - 존재하지 않은 댓글 접근")
    @Test
    void requestLike_failed_commentDoesNotExist() {
        //given
        int emptyCommentIdx = 5;
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
    void requestLike_failed_userIsLikeCommenter() {
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
}
