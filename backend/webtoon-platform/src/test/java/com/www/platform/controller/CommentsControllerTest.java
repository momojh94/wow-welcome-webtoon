package com.www.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.www.core.auth.entity.Users;
import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.platform.entity.Comments;
import com.www.platform.dto.*;
import com.www.platform.service.CommentsLikeDislikeService;
import com.www.platform.service.CommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CommentsController.class)
public class CommentsControllerTest {
    @MockBean
    private CommentsService commentsService;

    @MockBean
    private CommentsLikeDislikeService commentsLikeDislikeService;

    @MockBean
    private TokenChecker tokenChecker;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private RestDocumentationResultHandler documentationHandler;
    private Users user;
    private Webtoon webtoon;
    private Episode episode;
    private Comments comment;

    private static final String CODE = "code";
    private static final String MESSAGE = "msg";
    private static final String DATA = "data";
    private static final String AUTH_HEADER = "Authorization";
    private static final String ACCESS_TOKEN = "bearer eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkeCI6MiwidXNlcl9uYW1lIjoi6rmA7Ju57YiwIiwiaWF0IjoxNjE5NDM5NDA3LCJleHAiOjE2MTk0NDMwMDd9.uCpvXtkLvhcDZMhfy5mMpo9J9V96SdN_LtDU5Z3as_s";

    @BeforeEach
    void setUp(WebApplicationContext context,
               RestDocumentationContextProvider restDocumentation) {
        documentationHandler = document("{class-name}/{method-name}",
                preprocessRequest(prettyPrint(), modifyUris().removePort()),
                preprocessResponse(prettyPrint()));

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(documentationHandler)
                .alwaysDo(print())
                .build();

        objectMapper = new ObjectMapper();

        user = Users.builder()
                .idx(1)
                .id("id123")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();

        webtoon = Webtoon.builder()
                .idx(1)
                .title("웹툰 제목")
                .toon_type(0)
                .genre1(0)
                .genre2(0)
                .summary("웹툰 한줄 요약")
                .plot("줄거리")
                .thumbnail("thumbnailTest.jpg")
                .end_flag(0)
                .build();

        episode = Episode.builder()
                .idx(1)
                .ep_no(1)
                .title("에피소드 제목")
                .webtoon(webtoon)
                .author_comment("작가의 말")
                .build();

        comment = Comments.builder()
                .idx(1)
                .users(user)
                .ep(episode)
                .content("댓글 내용 1")
                .build();

    }

    @DisplayName("댓글 페이징 조회")
    @Test
    void getComments() throws Exception {
        //given
        String page = "1";
        int pageNumber = Integer.parseInt(page);
        List<Comments> commentsList = new ArrayList<>();
        for (int idx = 3; idx >= 1; idx--) {
            commentsList.add(Comments.builder()
                    .idx(idx)
                    .users(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .build());
        }
        CommentsResponseDto responseData = CommentsResponseDto.builder()
                .comments(commentsList.stream()
                        .map(CommentsDto::new)
                        .collect(Collectors.toList()))
                .total_pages(1)
                .build();

        Response<CommentsResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get comments by page request");
        response.setData(responseData);

        given(commentsService.getCommentsByPageRequest(episode.getIdx(), pageNumber))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/episodes/{ep_idx}/comments", episode.getIdx())
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : get comments by page request"))
                .andExpect(jsonPath("data.comments[0].idx").value(3))
                .andExpect(jsonPath("data.total_pages").value(1))
                .andDo(this.documentationHandler.document(
                        pathParameters(
                                parameterWithName("ep_idx").description("에피소드의 idx")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 (1 이상)")
                        ),
                        responseFields(
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터"),
                                fieldWithPath("data.comments[]").description("댓글 목록").type(JsonFieldType.ARRAY),
                                fieldWithPath("data.comments.[].idx").description("댓글 기본 idx").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].user_id").description("유저 아이디").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].like_cnt").description("좋아요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].dislike_cnt").description("싫어요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].content").description("댓글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].created_date").description("땟글 생성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.total_pages").description("총 페이지 수").type(JsonFieldType.NUMBER)
                        )));
    }

    @DisplayName("베스트 댓글 조회")
    @Test
    void getBestCommnets() throws Exception {
        //given
        List<Comments> commentsList = new ArrayList<>();
        for (int idx = 3; idx < 8; idx++) {
            commentsList.add(Comments.builder()
                    .idx(idx)
                    .users(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .like_cnt(30 - idx)
                    .dislike_cnt(10 + idx)
                    .build());
        }
        List<CommentsDto> responseData = commentsList.stream()
                .map(CommentsDto::new)
                .collect(Collectors.toList());

        Response<List<CommentsDto>> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get best comments");
        response.setData(responseData);

        given(commentsService.getBestComments(episode.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/episodes/{ep_idx}/comments/best", episode.getIdx())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : get best comments"))
                .andExpect(jsonPath("data[0].idx").value(3))
                .andExpect(jsonPath("data[4].idx").value(7));
    }

    @DisplayName("댓글 등록")
    @Test
    void insertComments() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(
                new CommentsSaveRequestDto(comment.getContent()));

        Response<Integer> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : insert comment");
        response.setData(comment.getIdx());

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentsService.insertComments(user.getIdx(), episode.getIdx(), comment.getContent()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/episodes/{ep_idx}/comments", episode.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : insert comment"))
                .andExpect(jsonPath(DATA).value(comment.getIdx()));
    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComments() throws Exception {
        //given
        Response<Integer> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : delete comment");
        response.setData(comment.getIdx());

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentsService.deleteComments(user.getIdx(), comment.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(delete("/comments/{cmt_idx}", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : delete comment"))
                .andExpect(jsonPath(DATA).value(comment.getIdx()));
    }

    @DisplayName("댓글 좋아요 요청")
    @Test
    void requestCommentsLike() throws Exception {
        //given
        CommentsLikeDislikeCntResponseDto responseData =
                new CommentsLikeDislikeCntResponseDto(comment.getLike_cnt() + 1);
        Response<CommentsLikeDislikeCntResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : success request like");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentsLikeDislikeService.requestLike(user.getIdx(), comment.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/comments/{cmt_idx}/like", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : success request like"))
                .andExpect(jsonPath("data.cnt").value(comment.getLike_cnt() + 1));
    }

    @DisplayName("댓글 싫어요 요청")
    @Test
    void requestCommentsDislike() throws Exception {
        //given
        CommentsLikeDislikeCntResponseDto responseData =
                new CommentsLikeDislikeCntResponseDto(comment.getDislike_cnt() + 1);
        Response<CommentsLikeDislikeCntResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : success request dislike");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentsLikeDislikeService.requestDislike(user.getIdx(), comment.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/comments/{cmt_idx}/dislike", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : success request dislike"))
                .andExpect(jsonPath("data.cnt").value(comment.getDislike_cnt() + 1));
    }

    @DisplayName("마이페이지 내가쓴 댓글 조회")
    @Test
    void getMyPageComments() throws Exception {
        //given
        String page = "1";
        int pageNumber = Integer.parseInt(page);
        List<Comments> commentsList = new ArrayList<>();
        for (int idx = 13; idx >= 9; idx--) {
            commentsList.add(Comments.builder()
                    .idx(idx)
                    .users(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .build());
        }
        MyPageCommentsResponseDto responseData
                = MyPageCommentsResponseDto.builder()
                .comments(commentsList.stream()
                        .map(MyPageCommentsDto::new)
                        .collect(Collectors.toList()))
                .total_pages(1)
                .build();

        Response<MyPageCommentsResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get my page comments");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentsService.getMyPageComments(user.getIdx(), pageNumber))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/users/comments", episode.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : get my page comments"))
                .andExpect(jsonPath("data.comments[0].idx").value(13))
                .andExpect(jsonPath("data.comments[4].idx").value(9))
                .andExpect(jsonPath("data.total_pages").value(1));
    }
}

