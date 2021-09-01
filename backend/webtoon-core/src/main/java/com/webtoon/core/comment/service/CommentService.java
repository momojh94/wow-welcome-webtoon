package com.webtoon.core.comment.service;

import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.comment.repository.CommentDislikeRepository;
import com.webtoon.core.comment.repository.CommentLikeRepository;
import com.webtoon.core.comment.repository.CommentRepository;
import com.webtoon.core.comment.dto.CommentResponse;
import com.webtoon.core.comment.dto.CommentsResponse;
import com.webtoon.core.comment.dto.MyPageCommentResponse;
import com.webtoon.core.comment.dto.MyPageCommentsResponse;
import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.episode.repository.EpisodeRepository;
import com.webtoon.core.user.domain.User;
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

@Service
public class CommentService {
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentDislikeRepository commentDislikeRepository;
    private EpisodeRepository episodeRepository;

    //한 페이지 내의 최대 댓글 갯수
    public static final int COMMENTS_COUNT_PER_PAGE = 15;
    public static final int MYPAGE_COMMENTS_COUNT_PER_PAGE = 10;

    public CommentService(CommentRepository commentRepository,
                          CommentLikeRepository commentLikeRepository,
                          CommentDislikeRepository commentDislikeRepository,
                          EpisodeRepository episodeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.commentDislikeRepository = commentDislikeRepository;
        this.episodeRepository = episodeRepository;
    }

    @Transactional(readOnly = true)
    public CommentsResponse findAllComment(Long epIdx, int page) {
        page = page == 0 ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByEpIdx(pageable, epIdx);

        /*if (page > commentsPage.getTotalPages()) {
            //
        }*/

        return CommentsResponse.builder()
                               .comments(commentsPage.stream()
                                                     .map(CommentResponse::new)
                                                     .collect(Collectors.toList()))
                               .totalPages(commentsPage.getTotalPages())
                               .build();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findAllBestComment(Long epIdx) {
        return commentRepository.findBestCommentsByEpIdx(epIdx)
                                .map(CommentResponse::new)
                                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MyPageCommentsResponse findAllMyPageComment(User user, int page){
        page = page == 0 ? 1 : page;
        Long userIdx = user.getIdx();
        Pageable pageable = PageRequest.of(page - 1, MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByUser(pageable, user);

        /*if (page > commentsPage.getTotalPages()) {
            //
        }*/

        return MyPageCommentsResponse.builder()
                                     .comments(commentsPage.stream()
                                                           .map(MyPageCommentResponse::new)
                                                           .collect(Collectors.toList()))
                                     .totalPages(commentsPage.getTotalPages())
                                     .build();
    }

    @Transactional
    public void create(User user, Long epIdx, String content) {
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
    public void delete(User user, Long commentIdx) {
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
