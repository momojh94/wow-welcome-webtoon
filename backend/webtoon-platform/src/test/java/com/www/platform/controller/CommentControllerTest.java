package com.www.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.www.core.auth.enums.Gender;
import com.www.core.auth.entity.User;
import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.file.enums.EndFlag;
import com.www.core.file.enums.StoryGenre;
import com.www.core.file.enums.StoryType;
import com.www.core.platform.entity.Comment;
import com.www.platform.dto.*;
import com.www.platform.service.CommentLikeDislikeService;
import com.www.platform.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest {
    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentLikeDislikeService commentLikeDislikeService;

    @MockBean
    private TokenChecker tokenChecker;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private RestDocumentationResultHandler documentationHandler;
    private User user;
    private Webtoon webtoon;
    private Episode episode;
    private Comment comment;

    private static final String CODE = "code";
    private static final String MESSAGE = "msg";
    private static final String DATA = "data";
    private static final String AUTH_HEADER = "AUTHORIZATION";
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

        user = User.builder()
                .idx(1L)
                .account("id123")
                .name("철수")
                .pw("1q2w3e4r")
                .gender(Gender.MALE)
                .email("test@email.com")
                .build();

        webtoon = Webtoon.builder()
                .idx(1L)
                .title("웹툰 제목")
                .storyType(StoryType.EPISODE)
                .storyGenre1(StoryGenre.DAILY)
                .storyGenre2(StoryGenre.GAG)
                .summary("웹툰 한줄 요약")
                .plot("줄거리")
                .thumbnail("thumbnailTest.jpg")
                .endFlag(EndFlag.ONGOING)
                .build();

        episode = Episode.builder()
                .idx(1L)
                .epNo(1)
                .title("에피소드 제목")
                .webtoon(webtoon)
                .authorComment("작가의 말")
                .build();

        comment = Comment.builder()
                .idx(1L)
                .user(user)
                .ep(episode)
                .content("댓글 내용 1")
                .createdDate(LocalDateTime.of(2021, 4, 22, 05, 32))
                .build();

    }

    @DisplayName("댓글 페이징 조회")
    @Test
    void getComments() throws Exception {
        //given
        String page = "1";
        int pageNumber = Integer.parseInt(page);
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 3L; idx >= 1L; idx--) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .createdDate(LocalDateTime.of(2021, 4, 22, (int)(idx + 0), 32))
                    .build());
        }
        CommentsResponseDto responseData = CommentsResponseDto.builder()
                .comments(commentList.stream()
                        .map(CommentDto::new)
                        .collect(Collectors.toList()))
                .totalPages(1)
                .build();

        Response<CommentsResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get comments by page request");
        response.setData(responseData);

        given(commentService.getCommentsByPageRequest(episode.getIdx(), pageNumber))
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
                                fieldWithPath("data.comments.[].account").description("유저 계정 아이디").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].like_count").description("좋아요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].dislike_count").description("싫어요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].content").description("댓글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].created_date").description("댓글 생성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.total_pages").description("총 페이지 수").type(JsonFieldType.NUMBER)
                        )));
    }

    @DisplayName("베스트 댓글 조회")
    @Test
    void getBestCommnets() throws Exception {
        //given
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 3L; idx < 8; idx++) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .likeCount((int)(30 - idx))
                    .dislikeCount((int)(10 + idx))
                    .createdDate(LocalDateTime.of(2021, 4, 22, (int)(idx + 0), 32))
                    .build());
        }
        List<CommentDto> responseData = commentList.stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());

        Response<List<CommentDto>> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get best comments");
        response.setData(responseData);

        given(commentService.getBestComments(episode.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/episodes/{ep_idx}/comments/best", episode.getIdx())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : get best comments"))
                .andExpect(jsonPath("data[0].idx").value(3))
                .andExpect(jsonPath("data[4].idx").value(7))
                .andDo(this.documentationHandler.document(
                        pathParameters(
                                parameterWithName("ep_idx").description("에피소드의 idx")
                        ),
                        responseFields(
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터"),
                                fieldWithPath("data[]").description("베스트 댓글 목록(최대 5개)").type(JsonFieldType.ARRAY),
                                fieldWithPath("data[].idx").description("댓글 기본 idx").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].account").description("유저 계정 아이디").type(JsonFieldType.STRING),
                                fieldWithPath("data[].like_count").description("좋아요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].dislike_count").description("싫어요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].content").description("댓글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data[].created_date").description("댓글 생성일").type(JsonFieldType.STRING)
                        )));
    }

    @DisplayName("댓글 등록")
    @Test
    void insertComment() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(
                new CommentSaveRequestDto(comment.getContent()));

        Response<Long> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : insert comment");
        response.setData(null);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentService.insertComment(user.getIdx(), episode.getIdx(), comment.getContent()))
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
                .andExpect(jsonPath(DATA).doesNotExist())
                .andDo(this.documentationHandler.document(
                        requestHeaders(
                                headerWithName(AUTH_HEADER).description("유저의 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("ep_idx").description("에피소드의 idx")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터")
                        )));

    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComment() throws Exception {
        //given
        Response<Long> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : delete comment");
        response.setData(null);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentService.deleteComment(user.getIdx(), comment.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(delete("/comments/{cmt_idx}", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : delete comment"))
                .andExpect(jsonPath(DATA).doesNotExist())
                .andDo(this.documentationHandler.document(
                        requestHeaders(
                                headerWithName(AUTH_HEADER).description("유저의 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("cmt_idx").description("댓글의 idx")
                        ),
                        responseFields(
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터")
                        )));

    }

    @DisplayName("댓글 좋아요 요청")
    @Test
    void requestCommentLike() throws Exception {
        //given
        CommentLikeDislikeCountResponseDto responseData =
                new CommentLikeDislikeCountResponseDto(comment.getLikeCount() + 1);
        Response<CommentLikeDislikeCountResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : success request like");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentLikeDislikeService.requestLike(user.getIdx(), comment.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/comments/{cmt_idx}/like", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : success request like"))
                .andExpect(jsonPath("data.count").value(comment.getLikeCount() + 1))
                .andDo(this.documentationHandler.document(
                        requestHeaders(
                                headerWithName(AUTH_HEADER).description("유저의 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("cmt_idx").description("댓글의 idx")
                        ),
                        responseFields(
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터"),
                                fieldWithPath("data.count").description("좋아요 수").type(JsonFieldType.NUMBER)
                        )));
    }

    @DisplayName("댓글 싫어요 요청")
    @Test
    void requestCommentDislike() throws Exception {
        //given
        CommentLikeDislikeCountResponseDto responseData =
                new CommentLikeDislikeCountResponseDto(comment.getDislikeCount() + 1);
        Response<CommentLikeDislikeCountResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : success request dislike");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentLikeDislikeService.requestDislike(user.getIdx(), comment.getIdx()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/comments/{cmt_idx}/dislike", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : success request dislike"))
                .andExpect(jsonPath("data.count").value(comment.getDislikeCount() + 1))
                .andDo(this.documentationHandler.document(
                        requestHeaders(
                                headerWithName(AUTH_HEADER).description("유저의 AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("cmt_idx").description("댓글의 idx")
                        ),
                        responseFields(
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터"),
                                fieldWithPath("data.count").description("싫어요 수").type(JsonFieldType.NUMBER)
                        )));
    }

    @DisplayName("마이페이지 내가쓴 댓글 조회")
    @Test
    void getMyPageComments() throws Exception {
        //given
        String page = "1";
        int pageNumber = Integer.parseInt(page);
        List<Comment> commentList = new ArrayList<>();
        for (Long idx = 13L; idx >= 9L; idx--) {
            commentList.add(Comment.builder()
                    .idx(idx)
                    .user(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .createdDate(LocalDateTime.of(2021, 4, 22, (int)(idx + 0), 32))
                    .build());
        }
        MyPageCommentsResponseDto responseData
                = MyPageCommentsResponseDto.builder()
                .comments(commentList.stream()
                        .map(MyPageCommentDto::new)
                        .collect(Collectors.toList()))
                .totalPages(1)
                .build();

        Response<MyPageCommentsResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get my page comments");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentService.getMyPageComments(user.getIdx(), pageNumber))
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
                .andExpect(jsonPath("data.total_pages").value(1))
                .andDo(this.documentationHandler.document(
                        requestHeaders(
                                headerWithName(AUTH_HEADER).description("유저의 AccessToken")
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
                                fieldWithPath("data.comments.[].webtoon_thumbnail").description("웹툰 썸네일").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].webtoon_title").description("웹툰 제목").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].ep_no").description("에피소드 회차").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].like_count").description("좋아요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].dislike_count").description("싫어요 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.comments.[].content").description("댓글 내용").type(JsonFieldType.STRING),
                                fieldWithPath("data.comments.[].created_date").description("댓글 생성일").type(JsonFieldType.STRING),
                                fieldWithPath("data.total_pages").description("총 페이지 수").type(JsonFieldType.NUMBER)
                        )));
    }
}

