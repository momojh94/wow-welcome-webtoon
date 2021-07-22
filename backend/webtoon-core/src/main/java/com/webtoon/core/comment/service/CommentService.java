package com.webtoon.core.comment.service;

import com.webtoon.core.comment.dto.CommentResponseDto;
import com.webtoon.core.comment.dto.CommentsResponseDto;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.episode.domain.EpisodeRepository;
import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.comment.domain.CommentDislikeRepository;
import com.webtoon.core.comment.domain.CommentLikeRepository;
import com.webtoon.core.comment.domain.CommentRepository;
import com.webtoon.core.comment.dto.MyPageCommentResponseDto;
import com.webtoon.core.comment.dto.MyPageCommentsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.webtoon.core.common.exception.ErrorType.COMMENT_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.EPISODE_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.USER_IS_NOT_COMMENTER;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentDislikeRepository commentDislikeRepository;
    private UserRepository userRepository;
    private EpisodeRepository episodeRepository;

    //한 페이지 내의 최대 댓글 갯수
    public static final int COMMENTS_COUNT_PER_PAGE = 15;
    public static final int MYPAGE_COMMENTS_COUNT_PER_PAGE = 10;

    public CommentService(CommentRepository commentRepository,
                          CommentLikeRepository commentLikeRepository,
                          CommentDislikeRepository commentDislikeRepository,
                          UserRepository userRepository,
                          EpisodeRepository episodeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.commentDislikeRepository = commentDislikeRepository;
        this.userRepository = userRepository;
        this.episodeRepository = episodeRepository;
    }

    @Transactional(readOnly = true)
    public CommentsResponseDto getCommentsByPageRequest(Long epIdx, int page) {
        page = page == 0 ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByEpIdx(pageable, epIdx);

        /*if (page > commentsPage.getTotalPages()) {
            //
        }*/

        return CommentsResponseDto.builder()
                                  .comments(commentsPage.stream()
                                                        .map(CommentResponseDto::new)
                                                        .collect(Collectors.toList()))
                                  .totalPages(commentsPage.getTotalPages())
                                  .build();
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getBestComments(Long epIdx) {
        return commentRepository.findBestCommentsByEpIdx(epIdx)
                                .map(CommentResponseDto::new)
                                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MyPageCommentsResponseDto getMyPageComments(Long userIdx, int page){
        page = page == 0 ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByUserIdx(pageable, userIdx);

        /*if (page > commentsPage.getTotalPages()) {
            //
        }*/

        return MyPageCommentsResponseDto.builder()
                                        .comments(commentsPage.stream()
                                                              .map(MyPageCommentResponseDto::new)
                                                              .collect(Collectors.toList()))
                                        .totalPages(commentsPage.getTotalPages())
                                        .build();
    }

    @Transactional
    public void create(Long userIdx, Long epIdx, String content) {
        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
        Episode episode = episodeRepository.findById(epIdx)
                                           .orElseThrow(() -> new ApplicationException(EPISODE_NOT_FOUND));

        Comment comment = Comment.builder()
                                 .user(user)
                                 .ep(episode)
                                 .content(content)
                                 .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long userIdx, Long commentIdx) {
        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentIdx)
                                           .orElseThrow(() -> new ApplicationException(COMMENT_NOT_FOUND));

        if (!comment.wasWrittenBy(user)) {
            throw new ApplicationException(USER_IS_NOT_COMMENTER);
        }

        commentLikeRepository.deleteAllByCommentIdx(commentIdx);
        commentDislikeRepository.deleteAllBycommentIdx(commentIdx);
        commentRepository.deleteById(commentIdx);
    }
}
