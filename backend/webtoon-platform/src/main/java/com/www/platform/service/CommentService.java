package com.www.platform.service;

import com.www.core.auth.entity.User;
import com.www.core.auth.repository.UserRepository;
import com.www.core.common.Response;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * //import javax.transaction.Transactional;
 *
 * @Transactional에서 org.springframework.transaction.annotation.Transactional
 * 가 아니라에서 javax.transaction.Transactional같은 readOnly속성은
 * Spring 트랜잭션에 따라 다릅니다.
 */

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
    public Response<Long> insertComment(Long userIdx, Long epIdx, String content) {
        Response<Long> result = new Response<Long>();
        Optional<User> user = userRepository.findById(userIdx);
        Optional<Episode> episode = episodeRepository.findById(epIdx);

        if(200 < content.length()){
            result.setCode(27);
            result.setMsg("fail : content length is too long");
        }
        else if(!episode.isPresent()){ // 에피소드가 존재하지 않을 때
            result.setCode(20);
            result.setMsg("fail : episode doesn't exist");
        }
        else{   // 댓글 DB 저장
            Comment comment = Comment.builder()
                    .user(user.get())
                    .ep(episode.get())
                    .content(content)
                    .build();
            commentRepository.save(comment);

            result.setCode(0);
            result.setMsg("request complete : insert comment");
        }

        return result;
    }

    @Transactional
    public Response<Long> deleteComment(Long userIdx, Long commentIdx) {
        Response<Long> result = new Response<Long>();
        Optional<User> users = userRepository.findById(userIdx);
        Optional<Comment> comments = commentRepository.findById(commentIdx);

        if(comments.isPresent()){ // 유저가 해당 댓글의 주인이 아닐 때
            if(userIdx != comments.get().getUser().getIdx()){
                result.setCode(22);
                result.setMsg("fail : user isn't commenter");
            }
            else{   // 댓글 삭제
                commentLikeRepository.deleteAllByCommentIdx(commentIdx);
                commentDislikeRepository.deleteAllBycommentIdx(commentIdx);
                commentRepository.deleteById(commentIdx);
                result.setCode(0);
                result.setMsg("request complete : delete comment");
            }
        }
        else    // 댓글이 이미 없을 때
        {
            result.setCode(21);
            result.setMsg("fail : comment doesn't exist");
        }

        return result;
    }

    /**
     * get Comments List by Page number
     *
     * @param epIdx episode idx
     * @param page page number
     */
    @Transactional(readOnly = true)
    public Response<CommentsResponseDto> getCommentsByPageRequest(Long epIdx, int page) {
        Response<CommentsResponseDto> result = new Response<CommentsResponseDto>();

        if(!episodeRepository.existsById(epIdx)) {    // 에피소드가 존재하지 않을 때
            result.setCode(20);
            result.setMsg("fail : episode doesn't exist");
        }
        else{
            if(page < 1){
                result.setCode(23);
                result.setMsg("fail : invalid page number");
                return result;
            }

            Pageable pageable = PageRequest.of(page - 1, COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
            Page<Comment> commentsPage = commentRepository.findAllByEpIdx(pageable, epIdx);

            if(page > commentsPage.getTotalPages() && page != 1){
                result.setCode(23);
                result.setMsg("fail : invalid page number");
            }
            else{
                result.setCode(0);
                result.setMsg("request complete : get comments by page request");
                CommentsResponseDto commentsResponseDto
                        = CommentsResponseDto.builder()
                        .comments(commentsPage.stream()
                                .map(CommentDto::new)
                                .collect(Collectors.toList()))
                        .totalPages(commentsPage.getTotalPages())
                        .build();

                result.setData(commentsResponseDto);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Response<List<CommentDto>> getBestComments(Long epIdx) {
        Response<List<CommentDto>> result = new Response<List<CommentDto>>();

        if(!episodeRepository.existsById(epIdx)) {  // 에피소드가 존재하지 않을 때
            result.setCode(20);
            result.setMsg("fail : episode doesn't exist");
        }
        else{
            result.setData(commentRepository.findBestCommentsByEpIdx(epIdx)
                    .map(CommentDto::new)
                    .collect(Collectors.toList()));
            result.setCode(0);
            result.setMsg("requset complete : get best comments");
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Response<MyPageCommentsResponseDto> getMyPageComments(Long userIdx, int page){
        Response<MyPageCommentsResponseDto> result = new Response<MyPageCommentsResponseDto>();

        if(page < 1){
            result.setCode(23);
            result.setMsg("fail : invalid page number");
            return result;
        }

        Pageable pageable = PageRequest.of(page - 1, MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comment> commentsPage = commentRepository.findAllByUserIdx(pageable, userIdx);

        if (page > commentsPage.getTotalPages() && page != 1) {
            result.setCode(23);
            result.setMsg("fail : invalid page number");
            return result;
        }

        MyPageCommentsResponseDto myPageCommentsResponseDto
                = MyPageCommentsResponseDto.builder()
                .comments(commentsPage.stream()
                        .map(MyPageCommentDto::new)
                        .collect(Collectors.toList()))
                .totalPages(commentsPage.getTotalPages())
                .build();
        result.setCode(0);
        result.setMsg("request complete : get my page comments");
        result.setData(myPageCommentsResponseDto);
        return result;
    }
}
