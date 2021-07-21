package com.webtoon.api.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.common.service.TokenChecker;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.comment.domain.Comment;
import com.webtoon.api.comment.dto.CommentResponseDto;
import com.webtoon.api.comment.dto.CommentCreateRequestDto;
import com.webtoon.api.comment.dto.CommentsResponseDto;
import com.webtoon.api.comment.dto.MyPageCommentResponseDto;
import com.webtoon.api.comment.dto.MyPageCommentsResponseDto;
import com.webtoon.api.comment.service.CommentService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest {

    @MockBean
    private CommentService commentService;

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

    private static final String ERROR_CODE = "error_code";
    private static final String MESSAGE = "message";
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
                                                                                   .map(CommentResponseDto::new)
                                                                                   .collect(Collectors.toList()))
                                                              .totalPages(1)
                                                              .build();

        given(commentService.getCommentsByPageRequest(episode.getIdx(), pageNumber)).willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(get("/episodes/{ep_idx}/comments", episode.getIdx())
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
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
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
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
        List<CommentResponseDto> responseData = commentList.stream()
                                                           .map(CommentResponseDto::new)
                                                           .collect(Collectors.toList());

        given(commentService.getBestComments(episode.getIdx()))
                .willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(get("/episodes/{ep_idx}/comments/best", episode.getIdx())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath("data[0].idx").value(3))
              .andExpect(jsonPath("data[4].idx").value(7))
              .andDo(this.documentationHandler.document(
                      pathParameters(
                              parameterWithName("ep_idx").description("에피소드의 idx")
                      ),
                      responseFields(
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
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
                                                                .map(MyPageCommentResponseDto::new)
                                                                .collect(Collectors.toList()))
                                           .totalPages(1)
                                           .build();

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(commentService.getMyPageComments(user.getIdx(), pageNumber)).willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(get("/users/comments", episode.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON));


        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
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
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
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

    @DisplayName("댓글 등록")
    @Test
    void createComment() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(
                new CommentCreateRequestDto(comment.getContent()));

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());

        //when
        ResultActions result = mockMvc.perform(post("/episodes/{ep_idx}/comments", episode.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
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
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
                              fieldWithPath(MESSAGE).description("응답 메시지"),
                              subsectionWithPath(DATA).description("응답 데이터")
                      )));
    }

    @DisplayName("댓글 삭제")
    @Test
    void deleteComment() throws Exception {
        //given
        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());

        //when
        ResultActions result = mockMvc.perform(delete("/comments/{cmt_idx}", comment.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath(DATA).doesNotExist())
              .andDo(this.documentationHandler.document(
                      requestHeaders(
                              headerWithName(AUTH_HEADER).description("유저의 AccessToken")
                      ),
                      pathParameters(
                              parameterWithName("cmt_idx").description("댓글의 idx")
                      ),
                      responseFields(
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
                              fieldWithPath(MESSAGE).description("응답 메시지"),
                              subsectionWithPath(DATA).description("응답 데이터")
                      )));
    }
}