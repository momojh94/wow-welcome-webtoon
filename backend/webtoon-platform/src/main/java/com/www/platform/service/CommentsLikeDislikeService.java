package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;
import com.www.core.common.Response;
import com.www.core.platform.entity.Comments;
import com.www.core.platform.entity.CommentsDislike;
import com.www.core.platform.entity.CommentsLike;
import com.www.core.platform.repository.CommentsDislikeRepository;
import com.www.core.platform.repository.CommentsLikeRepository;
import com.www.core.platform.repository.CommentsRepository;
import com.www.platform.dto.CommentsLikeDislikeCntResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentsLikeDislikeService {
    private UsersRepository usersRepository;
    private CommentsRepository commentsRepository;
    private CommentsLikeRepository commentsLikeRepository;
    private CommentsDislikeRepository commentsDislikeRepository;

    @Transactional
    public Response<CommentsLikeDislikeCntResponseDto> requestLike(Long userIdx, Long commentIdx) {
        Response<CommentsLikeDislikeCntResponseDto> result = new Response<CommentsLikeDislikeCntResponseDto>();

        Optional<Users> users = usersRepository.findById(userIdx);
        Optional<Comments> comments = commentsRepository.findById(commentIdx);

        if(!comments.isPresent()){
            result.setCode(21);
            result.setMsg("fail : comment doesn't exist");
        }
        else if(users.get().getIdx() == comments.get().getUser().getIdx()){
            result.setCode(24);
            result.setMsg("fail : users can't request like on their own comments");
        }
        else{
            CommentsLike commentsLike;

            if(commentsLikeRepository.existsByCommentIdxAndUserIdx
                    (commentIdx, userIdx)){
                // complete : cancel request like

                commentsLike = commentsLikeRepository.findByCommentIdxAndUserIdx
                        (commentIdx, userIdx);
                commentsLikeRepository.deleteByIdx(commentsLike.getIdx());
                commentsRepository.updateLikeCount(comments.get().getIdx(), -1);

                CommentsLikeDislikeCntResponseDto commentsLikeDislikeCntResponseDto =
                        new CommentsLikeDislikeCntResponseDto(comments.get().getLikeCount() - 1);
                result.setCode(0);
                result.setMsg("request complete : cancel request like");
                result.setData(commentsLikeDislikeCntResponseDto);
            }
            else{
                // complete : success request like
                commentsLike = CommentsLike.builder()
                        .user(users.get())
                        .comment(comments.get())
                        .build();
                commentsLikeRepository.save(commentsLike);
                commentsRepository.updateLikeCount(comments.get().getIdx(), 1);

                CommentsLikeDislikeCntResponseDto commentsLikeDislikeCntResponseDto =
                        new CommentsLikeDislikeCntResponseDto(comments.get().getLikeCount() + 1);

                result.setCode(0);
                result.setMsg("request complete : success request like");
                result.setData(commentsLikeDislikeCntResponseDto);
            }
        }
        return result;
    }


    @Transactional
    public Response<CommentsLikeDislikeCntResponseDto> requestDislike(Long userIdx, Long commentIdx) {
        Response<CommentsLikeDislikeCntResponseDto> result = new Response<CommentsLikeDislikeCntResponseDto>();

        Optional<Users> users = usersRepository.findById(userIdx);
        Optional<Comments> comments = commentsRepository.findById(commentIdx);

        if(!comments.isPresent()){
            result.setCode(21);
            result.setMsg("fail : comment doesn't exist");
        }
        else if(users.get().getIdx() == comments.get().getUser().getIdx()){
            result.setCode(25);
            result.setMsg("fail : users can't request dislike on their own comments");
        }
        else{
            CommentsDislike commentsDislike;

            if(commentsDislikeRepository.existsByCommentIdxAndUserIdx
                    (commentIdx, userIdx)){
                // complete : cancel request dislike

                commentsDislike = commentsDislikeRepository.findByCommentIdxAndUserIdx
                        (commentIdx, userIdx);
                commentsDislikeRepository.deleteByIdx(commentsDislike.getIdx());
                commentsRepository.updateDislikeCount(comments.get().getIdx(), -1);

                CommentsLikeDislikeCntResponseDto commentsLikeDislikeCntResponseDto =
                        new CommentsLikeDislikeCntResponseDto(comments.get().getDislikeCount() - 1);
                result.setCode(0);
                result.setMsg("request complete : cancel request dislike");
                result.setData(commentsLikeDislikeCntResponseDto);
            }
            else{
                // complete : success request dislike
                commentsDislike = CommentsDislike.builder()
                        .user(users.get())
                        .comment(comments.get())
                        .build();
                commentsDislikeRepository.save(commentsDislike);
                commentsRepository.updateDislikeCount(comments.get().getIdx(), 1);

                CommentsLikeDislikeCntResponseDto commentsLikeDislikeCntResponseDto =
                        new CommentsLikeDislikeCntResponseDto(comments.get().getDislikeCount() + 1);
                result.setCode(0);
                result.setMsg("request complete : success request dislike");
                result.setData(commentsLikeDislikeCntResponseDto);
            }
        }
        return result;
    }
}
