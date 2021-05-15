package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
import com.www.core.platform.entity.Comment;
import com.www.core.platform.entity.CommentDislike;
import com.www.core.platform.entity.CommentLike;
import com.www.core.platform.repository.CommentDislikeRepository;
import com.www.core.platform.repository.CommentLikeRepository;
import com.www.core.platform.repository.CommentRepository;
import com.www.platform.dto.CommentLikeDislikeCountResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentLikeDislikeService {
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private CommentLikeRepository commentLikeRepository;
    private CommentDislikeRepository commentDislikeRepository;

    @Transactional
    public Response<CommentLikeDislikeCountResponseDto> requestLike(Long userIdx, Long commentIdx) {
        Response<CommentLikeDislikeCountResponseDto> result = new Response<CommentLikeDislikeCountResponseDto>();

        Optional<User> users = userRepository.findById(userIdx);
        Optional<Comment> comments = commentRepository.findById(commentIdx);

        if(!comments.isPresent()){
            result.setCode(21);
            result.setMsg("fail : comment doesn't exist");
        }
        else if(users.get().getIdx() == comments.get().getUser().getIdx()){
            result.setCode(24);
            result.setMsg("fail : users can't request like on their own comments");
        }
        else{
            CommentLike commentLike;

            if(commentLikeRepository.existsByCommentIdxAndUserIdx
                    (commentIdx, userIdx)){
                // complete : cancel request like

                commentLike = commentLikeRepository.findByCommentIdxAndUserIdx
                        (commentIdx, userIdx);
                commentLikeRepository.deleteByIdx(commentLike.getIdx());
                commentRepository.updateLikeCount(comments.get().getIdx(), -1);

                CommentLikeDislikeCountResponseDto commentLikeDislikeCountResponseDto =
                        new CommentLikeDislikeCountResponseDto(comments.get().getLikeCount() - 1);
                result.setCode(0);
                result.setMsg("request complete : cancel request like");
                result.setData(commentLikeDislikeCountResponseDto);
            }
            else{
                // complete : success request like
                commentLike = CommentLike.builder()
                        .user(users.get())
                        .comment(comments.get())
                        .build();
                commentLikeRepository.save(commentLike);
                commentRepository.updateLikeCount(comments.get().getIdx(), 1);

                CommentLikeDislikeCountResponseDto commentLikeDislikeCountResponseDto =
                        new CommentLikeDislikeCountResponseDto(comments.get().getLikeCount() + 1);

                result.setCode(0);
                result.setMsg("request complete : success request like");
                result.setData(commentLikeDislikeCountResponseDto);
            }
        }
        return result;
    }


    @Transactional
    public Response<CommentLikeDislikeCountResponseDto> requestDislike(Long userIdx, Long commentIdx) {
        Response<CommentLikeDislikeCountResponseDto> result = new Response<CommentLikeDislikeCountResponseDto>();

        Optional<User> users = userRepository.findById(userIdx);
        Optional<Comment> comments = commentRepository.findById(commentIdx);

        if(!comments.isPresent()){
            result.setCode(21);
            result.setMsg("fail : comment doesn't exist");
        }
        else if(users.get().getIdx() == comments.get().getUser().getIdx()){
            result.setCode(25);
            result.setMsg("fail : users can't request dislike on their own comments");
        }
        else{
            CommentDislike commentDislike;

            if(commentDislikeRepository.existsByCommentIdxAndUserIdx
                    (commentIdx, userIdx)){
                // complete : cancel request dislike

                commentDislike = commentDislikeRepository.findByCommentIdxAndUserIdx
                        (commentIdx, userIdx);
                commentDislikeRepository.deleteByIdx(commentDislike.getIdx());
                commentRepository.updateDislikeCount(comments.get().getIdx(), -1);

                CommentLikeDislikeCountResponseDto commentLikeDislikeCountResponseDto =
                        new CommentLikeDislikeCountResponseDto(comments.get().getDislikeCount() - 1);
                result.setCode(0);
                result.setMsg("request complete : cancel request dislike");
                result.setData(commentLikeDislikeCountResponseDto);
            }
            else{
                // complete : success request dislike
                commentDislike = CommentDislike.builder()
                        .user(users.get())
                        .comment(comments.get())
                        .build();
                commentDislikeRepository.save(commentDislike);
                commentRepository.updateDislikeCount(comments.get().getIdx(), 1);

                CommentLikeDislikeCountResponseDto commentLikeDislikeCountResponseDto =
                        new CommentLikeDislikeCountResponseDto(comments.get().getDislikeCount() + 1);
                result.setCode(0);
                result.setMsg("request complete : success request dislike");
                result.setData(commentLikeDislikeCountResponseDto);
            }
        }
        return result;
    }
}
