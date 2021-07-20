package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.enums.Gender;
import com.www.core.auth.repository.UserRepository;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.file.enums.EndFlag;
import com.www.core.file.enums.StoryGenre;
import com.www.core.file.enums.StoryType;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.platform.entity.Comment;
import com.www.core.platform.repository.CommentDislikeRepository;
import com.www.core.platform.repository.CommentLikeRepository;
import com.www.core.platform.repository.CommentRepository;
import com.www.platform.dto.CommentResponseDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.dto.MyPageCommentResponseDto;
import com.www.platform.dto.MyPageCommentsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

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
    private String contentWithinSpecifiedLength = "제한된 길이(500자) 이내의 댓글 내용";
    private final int CONTENT_MAX_LENGTH = 500;

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

        comment = Comment.builder()
                .idx(1L)
                .user(user)
                .ep(episode)
                .content(contentWithinSpecifiedLength)
                .build();
    }

    @DisplayName("댓글 저장 성공")
    @Test
    void createComment() {
        //given
        Long userIdx = user.getIdx();
        Long epIdx = episode.getIdx();
        given(userRepository.findById(userIdx)).willReturn(Optional.of(user));
        given(episodeRepository.findById(epIdx)).willReturn(Optional.of(episode));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        commentService.create(userIdx, epIdx, contentWithinSpecifiedLength);

        //then
        assertAll(
                () -> verify(commentRepository).save(any(Comment.class)),
                () -> verify(userRepository).findById(userIdx),
                () -> verify(episodeRepository).findById(epIdx)
        );
    }


    @DisplayName("댓글 삭제 성공")
    @Test
    void deleteComment() {
        //given
        Long userIdx = user.getIdx();
        Long commentIdx = comment.getIdx();
        given(userRepository.findById(userIdx)).willReturn(Optional.of(user));
        given(commentRepository.findById(commentIdx)).willReturn(Optional.of(comment));

        //when
        commentService.delete(user.getIdx(), comment.getIdx());

        //then
        assertAll(
                () -> verify(commentLikeRepository).deleteAllByCommentIdx(commentIdx),
                () -> verify(commentDisLikeRepository).deleteAllBycommentIdx(commentIdx),
                () -> verify(commentRepository).deleteById(commentIdx)
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

        Pageable pageable = PageRequest.of(page - 1,
                CommentService.COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = new PageImpl<>(subList, pageable, commentList.size());

        given(commentRepository.findAllByEpIdx(pageable, episode.getIdx())).willReturn(commentsPage);

        //when
        CommentsResponseDto result = commentService.getCommentsByPageRequest(episode.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(fromIndex),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getComments().size()).isEqualTo(CommentService.COMMENTS_COUNT_PER_PAGE),
                () -> assertThat(result.getTotalPages()).isEqualTo(commentsPage.getTotalPages())
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

        given(commentRepository.findBestCommentsByEpIdx(epIdx))
                .willReturn(bestCommentList.stream());

        //when
        List<CommentResponseDto> result = commentService.getBestComments(epIdx);

        //then
        assertAll(
                () -> assertArrayEquals(bestCommentList.stream()
                                                       .map(Comment::getIdx)
                                                       .toArray(Long[]::new),
                        result.stream()
                              .map(CommentResponseDto::getIdx)
                              .toArray(Long[]::new))
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

        Pageable pageable = PageRequest.of(page - 1,
                CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = new PageImpl<Comment>(subList, pageable, commentList.size());

        given(commentRepository.findAllByUserIdx(pageable, user.getIdx())).willReturn(commentsPage);

        //when
        MyPageCommentsResponseDto result = commentService.getMyPageComments(user.getIdx(), page);

        //then
        assertAll(
                () -> assertThat(commentsPage.getPageable().getOffset()).isEqualTo(fromIndex),
                () -> assertThat(commentsPage.getPageable().getPageNumber()).isEqualTo(page - 1),
                () -> assertThat(commentsPage.getPageable().getPageSize()).isEqualTo(CommentService.MYPAGE_COMMENTS_COUNT_PER_PAGE),

                () -> assertThat(result.getComments().size()).isEqualTo(commentsPage.getNumberOfElements()),
                () -> assertThat(result.getTotalPages()).isEqualTo(commentsPage.getTotalPages()),
                () -> assertArrayEquals(subList.stream()
                                               .map(Comment::getIdx)
                                               .toArray(Long[]::new),
                        result.getComments().stream()
                              .map(MyPageCommentResponseDto::getIdx)
                              .toArray(Long[]::new))
        );
    }

}
