package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.exception.BusinessException;
import com.www.core.file.entity.Episode;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.platform.entity.Comment;
import com.www.core.platform.repository.CommentDislikeRepository;
import com.www.core.platform.repository.CommentLikeRepository;
import com.www.core.platform.repository.CommentRepository;
import com.www.platform.dto.CommentDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.dto.MyPageCommentDto;
import com.www.platform.dto.MyPageCommentsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.www.core.common.exception.ErrorType.COMMENT_NOT_FOUND;
import static com.www.core.common.exception.ErrorType.EPISODE_NOT_FOUND;
import static com.www.core.common.exception.ErrorType.INVALID_PAGE_VALUE;
import static com.www.core.common.exception.ErrorType.USER_IS_NOT_COMMENTER;
import static com.www.core.common.exception.ErrorType.USER_NOT_FOUND;

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

    // 예외 발생시 모든 DB작업 초기화 해주는 어노테이션 ( 완료시에만 커밋해줌 )
    @Transactional
    public void create(Long userIdx, Long epIdx, String content) {
        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        Episode episode = episodeRepository.findById(epIdx)
                                           .orElseThrow(() -> new BusinessException(EPISODE_NOT_FOUND));

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
                                  .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentIdx)
                                           .orElseThrow(() -> new BusinessException(COMMENT_NOT_FOUND));

        if (!comment.wasWrittenBy(user)) {
            throw new BusinessException(USER_IS_NOT_COMMENTER);
        }

        commentLikeRepository.deleteAllByCommentIdx(commentIdx);
        commentDislikeRepository.deleteAllBycommentIdx(commentIdx);
        commentRepository.deleteById(commentIdx);
    }


    @Transactional(readOnly = true)
    public CommentsResponseDto getCommentsByPageRequest(Long epIdx, int page) {
        if (page < 0) {
            throw new BusinessException(INVALID_PAGE_VALUE);
        }
        page = page == 0 ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByEpIdx(pageable, epIdx);

        /*if (page > commentsPage.getTotalPages()) {
            //
        }*/

        return CommentsResponseDto.builder()
                                  .comments(commentsPage.stream()
                                                        .map(CommentDto::new)
                                                        .collect(Collectors.toList()))
                                  .totalPages(commentsPage.getTotalPages())
                                  .build();
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getBestComments(Long epIdx) {
        return commentRepository.findBestCommentsByEpIdx(epIdx)
                                .map(CommentDto::new)
                                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MyPageCommentsResponseDto getMyPageComments(Long userIdx, int page){
        if (page < 0) {
            throw new BusinessException(INVALID_PAGE_VALUE);
        }
        page = page == 0 ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByUserIdx(pageable, userIdx);

        /*if (page > commentsPage.getTotalPages()) {
            //
        }*/

        return MyPageCommentsResponseDto.builder()
                                        .comments(commentsPage.stream()
                                                              .map(MyPageCommentDto::new)
                                                              .collect(Collectors.toList()))
                                        .totalPages(commentsPage.getTotalPages())
                                        .build();
    }
}
