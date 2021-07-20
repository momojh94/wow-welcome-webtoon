package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.exception.ApplicationException;
import com.www.core.platform.entity.Comment;
import com.www.core.platform.entity.CommentDislike;
import com.www.core.platform.entity.CommentLike;
import com.www.core.platform.repository.CommentDislikeRepository;
import com.www.core.platform.repository.CommentLikeRepository;
import com.www.core.platform.repository.CommentRepository;
import com.www.platform.dto.CommentLikeDislikeCountResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.www.core.common.exception.ErrorType.BAD_COMMENT_DISLIKE_REQUEST;
import static com.www.core.common.exception.ErrorType.BAD_COMMENT_LIKE_REQUEST;
import static com.www.core.common.exception.ErrorType.COMMENT_NOT_FOUND;
import static com.www.core.common.exception.ErrorType.USER_NOT_FOUND;

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
    public CommentLikeDislikeCountResponseDto requestLike(Long userIdx, Long commentIdx) {
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

            CommentLikeDislikeCountResponseDto result =
                    new CommentLikeDislikeCountResponseDto(comment.getLikeCount() - 1);
            return result;
        }

        // 좋아요 +1
        CommentLike commentLike = CommentLike.builder()
                                 .user(user)
                                 .comment(comment)
                                 .build();
        commentLikeRepository.save(commentLike);
        commentRepository.updateLikeCount(comment.getIdx(), 1);

        CommentLikeDislikeCountResponseDto result =
                new CommentLikeDislikeCountResponseDto(comment.getLikeCount() + 1);

        return result;
    }


    @Transactional
    public CommentLikeDislikeCountResponseDto requestDislike(Long userIdx, Long commentIdx) {
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

            CommentLikeDislikeCountResponseDto result =
                    new CommentLikeDislikeCountResponseDto(comment.getDislikeCount() - 1);
            return result;
        }

        // 싫어요 +1
        CommentDislike commentDislike = CommentDislike.builder()
                                                      .user(user)
                                                      .comment(comment)
                                                      .build();
        commentDislikeRepository.save(commentDislike);
        commentRepository.updateDislikeCount(comment.getIdx(), 1);

        CommentLikeDislikeCountResponseDto result =
                new CommentLikeDislikeCountResponseDto(comment.getDislikeCount() + 1);

        return result;
    }
}
