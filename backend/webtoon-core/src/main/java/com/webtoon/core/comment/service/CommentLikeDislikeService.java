package com.webtoon.core.comment.service;

import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.comment.domain.CommentDislike;
import com.webtoon.core.comment.repository.CommentDislikeRepository;
import com.webtoon.core.comment.domain.CommentLike;
import com.webtoon.core.comment.repository.CommentLikeRepository;
import com.webtoon.core.comment.repository.CommentRepository;
import com.webtoon.core.comment.dto.CommentLikeDislikeCountResponse;
import com.webtoon.core.user.domain.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.webtoon.core.common.exception.ExceptionType.BAD_COMMENT_DISLIKE_REQUEST;
import static com.webtoon.core.common.exception.ExceptionType.BAD_COMMENT_LIKE_REQUEST;
import static com.webtoon.core.common.exception.ExceptionType.COMMENT_NOT_FOUND;

@Service
public class CommentLikeDislikeService {

    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentDislikeRepository commentDislikeRepository;

    public CommentLikeDislikeService(CommentRepository commentRepository,
                                     CommentLikeRepository commentLikeRepository,
                                     CommentDislikeRepository commentDislikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.commentDislikeRepository = commentDislikeRepository;
    }

    @Transactional
    public CommentLikeDislikeCountResponse requestLike(User user, Long commentIdx) {
        Comment comment = commentRepository.findById(commentIdx)
                                           .orElseThrow(COMMENT_NOT_FOUND::getException);

        if (comment.wasWrittenBy(user)) {
            throw BAD_COMMENT_LIKE_REQUEST.getException();
        }

        // 이미 눌렀던 좋아요 취소
        if (commentLikeRepository.existsByCommentIdxAndUser(commentIdx, user)) {
            CommentLike commentLike = commentLikeRepository.findByCommentIdxAndUser(commentIdx, user);

            commentLikeRepository.deleteByIdx(commentLike.getIdx());
            commentRepository.updateLikeCount(comment.getIdx(), -1);

            CommentLikeDislikeCountResponse result =
                    CommentLikeDislikeCountResponse.of(comment.getLikeCount() - 1);

            return result;
        }

        // 좋아요 +1
        CommentLike commentLike = CommentLike.builder()
                                             .user(user)
                                             .comment(comment)
                                             .build();
        commentLikeRepository.save(commentLike);
        commentRepository.updateLikeCount(comment.getIdx(), 1);

        CommentLikeDislikeCountResponse result =
                CommentLikeDislikeCountResponse.of(comment.getLikeCount() + 1);

        return result;
    }


    @Transactional
    public CommentLikeDislikeCountResponse requestDislike(User user, Long commentIdx) {
        Comment comment = commentRepository.findById(commentIdx)
                                           .orElseThrow(COMMENT_NOT_FOUND::getException);

        if (comment.wasWrittenBy(user)) {
            throw BAD_COMMENT_DISLIKE_REQUEST.getException();
        }

        // 이미 눌렀던 싫어요 취소
        if (commentDislikeRepository.existsByCommentIdxAndUser(commentIdx, user)) {
            CommentDislike commentDislike = commentDislikeRepository.findByCommentIdxAndUser(commentIdx, user);

            commentDislikeRepository.deleteByIdx(commentDislike.getIdx());
            commentRepository.updateDislikeCount(comment.getIdx(), -1);

            CommentLikeDislikeCountResponse result =
                    CommentLikeDislikeCountResponse.of(comment.getDislikeCount() - 1);

            return result;
        }

        // 싫어요 +1
        CommentDislike commentDislike = CommentDislike.builder()
                                                      .user(user)
                                                      .comment(comment)
                                                      .build();
        commentDislikeRepository.save(commentDislike);
        commentRepository.updateDislikeCount(comment.getIdx(), 1);

        CommentLikeDislikeCountResponse result =
                CommentLikeDislikeCountResponse.of(comment.getDislikeCount() + 1);

        return result;
    }
}