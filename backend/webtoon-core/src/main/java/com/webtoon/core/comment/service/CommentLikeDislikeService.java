package com.webtoon.core.comment.service;

import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.comment.domain.CommentDislike;
import com.webtoon.core.comment.domain.CommentDislikeRepository;
import com.webtoon.core.comment.domain.CommentLike;
import com.webtoon.core.comment.domain.CommentLikeRepository;
import com.webtoon.core.comment.domain.CommentRepository;
import com.webtoon.core.comment.dto.CommentLikeDislikeCountResponse;
import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.webtoon.core.common.exception.ErrorType.BAD_COMMENT_DISLIKE_REQUEST;
import static com.webtoon.core.common.exception.ErrorType.BAD_COMMENT_LIKE_REQUEST;
import static com.webtoon.core.common.exception.ErrorType.COMMENT_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;

@Service
public class CommentLikeDislikeService {
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentDislikeRepository commentDislikeRepository;

    public CommentLikeDislikeService(UserRepository userRepository,
                                     CommentRepository commentRepository,
                                     CommentLikeRepository commentLikeRepository,
                                     CommentDislikeRepository commentDislikeRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.commentDislikeRepository = commentDislikeRepository;
    }

    @Transactional
    public CommentLikeDislikeCountResponse requestLike(Long userIdx, Long commentIdx) {
        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentIdx)
                                           .orElseThrow(() -> new ApplicationException(COMMENT_NOT_FOUND));

        if (comment.wasWrittenBy(user)) {
            throw new ApplicationException(BAD_COMMENT_LIKE_REQUEST);
        }

        // 이미 눌렀던 좋아요 취소
        if (commentLikeRepository.existsByCommentIdxAndUserIdx(commentIdx, userIdx)) {
            CommentLike commentLike = commentLikeRepository
                    .findByCommentIdxAndUserIdx(commentIdx, userIdx);

            commentLikeRepository.deleteByIdx(commentLike.getIdx());
            commentRepository.updateLikeCount(comment.getIdx(), -1);

            CommentLikeDislikeCountResponse result =
                    new CommentLikeDislikeCountResponse(comment.getLikeCount() - 1);
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
                new CommentLikeDislikeCountResponse(comment.getLikeCount() + 1);

        return result;
    }


    @Transactional
    public CommentLikeDislikeCountResponse requestDislike(Long userIdx, Long commentIdx) {
        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentIdx)
                                           .orElseThrow(() -> new ApplicationException(COMMENT_NOT_FOUND));

        if (comment.wasWrittenBy(user)) {
            throw new ApplicationException(BAD_COMMENT_DISLIKE_REQUEST);
        }

        // 이미 눌렀던 싫어요 취소
        if (commentDislikeRepository.existsByCommentIdxAndUserIdx(commentIdx, userIdx)) {
            CommentDislike commentDislike = commentDislikeRepository
                    .findByCommentIdxAndUserIdx(commentIdx, userIdx);

            commentDislikeRepository.deleteByIdx(commentDislike.getIdx());
            commentRepository.updateDislikeCount(comment.getIdx(), -1);

            CommentLikeDislikeCountResponse result =
                    new CommentLikeDislikeCountResponse(comment.getDislikeCount() - 1);
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
                new CommentLikeDislikeCountResponse(comment.getDislikeCount() + 1);

        return result;
    }
}
