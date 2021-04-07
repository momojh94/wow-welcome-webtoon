package com.www.platform.service;

import com.www.core.auth.entity.Users;
import com.www.core.auth.repository.UsersRepository;

import com.www.core.common.Response;
import com.www.core.file.entity.Episode;
import com.www.core.file.repository.EpisodeRepository;
import com.www.core.platform.entity.Comments;
import com.www.core.platform.entity.CommentsDislike;
import com.www.core.platform.repository.CommentsDislikeRepository;
import com.www.core.platform.repository.CommentsLikeRepository;
import com.www.core.platform.repository.CommentsRepository;
import com.www.platform.dto.CommentsDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.dto.MyPageCommentsDto;
import com.www.platform.dto.MyPageCommentsResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * //import javax.transaction.Transactional;
 *
 * @Transactional에서 org.springframework.transaction.annotation.Transactional
 * 가 아니라에서 javax.transaction.Transactional같은 readOnly속성은
 * Spring 트랜잭션에 따라 다릅니다.
 */


@AllArgsConstructor
@Service
public class  CommentsService {
    private CommentsRepository commentsRepository;
    private CommentsLikeRepository commentsLikeRepository;
    private CommentsDislikeRepository commentsDislikeRepository;
    private UsersRepository usersRepository;
    private EpisodeRepository episodeRepository;

    //한 페이지 내의 최대 댓글 갯수
    public static final int COMMENTS_COUNT_PER_PAGE = 15;
    public static final int MYPAGE_COMMENTS_COUNT_PER_PAGE = 10;

    // 예외 발생시 모든 DB작업 초기화 해주는 어노테이션 ( 완료시에만 커밋해줌 )
    @Transactional
    public Response<Integer> insertComments(int userIdx, int epIdx, String content) {
        Response<Integer> result = new Response<Integer>();
        Optional<Users> user = usersRepository.findById(userIdx);
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
            Comments comments = Comments.builder()
                    .users(user.get())
                    .ep(episode.get())
                    .content(content)
                    .build();
            int entityIdx = commentsRepository.save(comments).getIdx();

            result.setCode(0);
            result.setMsg("request complete : insert comment");
        }

        return result;
    }

    @Transactional
    public Response<Integer> deleteComments(int userIdx, int commentsIdx) {
        Response<Integer> result = new Response<Integer>();
        Optional<Users> users = usersRepository.findById(userIdx);
        Optional<Comments> comments = commentsRepository.findById(commentsIdx);

        if(comments.isPresent()){ // 유저가 해당 댓글의 주인이 아닐 때
            if(userIdx != comments.get().getUsers().getIdx()){
                result.setCode(22);
                result.setMsg("fail : user isn't commenter");
            }
            else{   // 댓글 삭제
                commentsLikeRepository.deleteAllByCommentsIdx(commentsIdx);
                commentsDislikeRepository.deleteAllByCommentsIdx(commentsIdx);
                commentsRepository.deleteById(commentsIdx);
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
    public Response<CommentsResponseDto> getCommentsByPageRequest(int epIdx, int page) {
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
            Page<Comments> commentsPage = commentsRepository.findAllByEpIdx(pageable, epIdx);

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
                                .map(CommentsDto::new)
                                .collect(Collectors.toList()))
                        .total_pages(commentsPage.getTotalPages())
                        .build();

                result.setData(commentsResponseDto);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Response<List<CommentsDto>> getBestComments(int epIdx) {
        Response<List<CommentsDto>> result = new Response<List<CommentsDto>>();

        if(!episodeRepository.existsById(epIdx)) {  // 에피소드가 존재하지 않을 때
            result.setCode(20);
            result.setMsg("fail : episode doesn't exist");
        }
        else{
            result.setData(commentsRepository.findBestCommentsByEpIdx(epIdx)
                    .map(CommentsDto::new)
                    .collect(Collectors.toList()));
            result.setCode(0);
            result.setMsg("requset complete : get best comments");
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Response<MyPageCommentsResponseDto> getMyPageComments(int userIdx, int page){
        Response<MyPageCommentsResponseDto> result = new Response<MyPageCommentsResponseDto>();

        if(page < 1){
            result.setCode(23);
            result.setMsg("fail : invalid page number");
            return result;
        }

        Pageable pageable = PageRequest.of(page - 1, MYPAGE_COMMENTS_COUNT_PER_PAGE, Sort.Direction.DESC, "idx");
        Page<Comments> commentsPage = commentsRepository.findAllByUsersIdx(pageable, userIdx);

        if (page > commentsPage.getTotalPages() && page != 1) {
            result.setCode(23);
            result.setMsg("fail : invalid page number");
            return result;
        }

        MyPageCommentsResponseDto myPageCommentsResponseDto
                = MyPageCommentsResponseDto.builder()
                .comments(commentsPage.stream()
                        .map(MyPageCommentsDto::new)
                        .collect(Collectors.toList()))
                .total_pages(commentsPage.getTotalPages())
                .build();
        result.setCode(0);
        result.setMsg("request complete : get my page comments");
        result.setData(myPageCommentsResponseDto);
        return result;
    }
}
