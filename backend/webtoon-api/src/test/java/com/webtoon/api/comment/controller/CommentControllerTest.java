package com.webtoon.api.comment.controller;

import com.webtoon.api.ControllerTest;
import com.webtoon.core.comment.dto.CommentResponse;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import com.webtoon.api.common.ApiResponse;
import com.webtoon.core.episode.domain.Episode;

import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.comment.dto.CommentCreateRequest;
import com.webtoon.core.comment.dto.CommentsResponse;
import com.webtoon.core.comment.dto.MyPageCommentResponse;
import com.webtoon.core.comment.dto.MyPageCommentsResponse;
import com.webtoon.core.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest extends ControllerTest {

    @MockBean
    private CommentService commentService;

    private Webtoon webtoon;
    private Episode episode;
    private Comment comment;

    @BeforeEach
    void setUp() {
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
    void findComments() throws Exception {
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
        CommentsResponse responseData = CommentsResponse.builder()
                                                        .comments(commentList.stream()
                                                                             .map(CommentResponse::new)
                                                                             .collect(Collectors.toList()))
                                                        .totalPages(1)
                                                        .build();

        given(commentService.findComments(episode.getIdx(), pageNumber)).willReturn(responseData);

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
    void findBestCommnets() throws Exception {
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
        List<CommentResponse> responseData = commentList.stream()
                                                        .map(CommentResponse::new)
                                                        .collect(Collectors.toList());

        given(commentService.findBestComments(episode.getIdx()))
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
    void findMyPageComments() throws Exception {
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
        MyPageCommentsResponse responseData
                = MyPageCommentsResponse.builder()
                                        .comments(commentList.stream()
                                                             .map(MyPageCommentResponse::new)
                                                             .collect(Collectors.toList()))
                                        .totalPages(1)
                                        .build();

        given(commentService.findMyPageComments(user, pageNumber)).willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(get("/users/comments")
                .header(AUTHORIZATION, TEST_AUTHORIZATION_HEADER)
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
                              headerWithName(AUTHORIZATION).description("유저의 AccessToken")
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
                new CommentCreateRequest(comment.getContent()));

        //when
        ResultActions result = mockMvc.perform(post("/episodes/{ep_idx}/comments", episode.getIdx())
                .header(AUTHORIZATION, TEST_AUTHORIZATION_HEADER)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath(DATA).doesNotExist())
              .andDo(this.documentationHandler.document(
                      requestHeaders(
                              headerWithName(AUTHORIZATION).description("유저의 AccessToken")
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

        //when
        ResultActions result = mockMvc.perform(delete("/comments/{cmt_idx}", comment.getIdx())
                .header(AUTHORIZATION, TEST_AUTHORIZATION_HEADER)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath(DATA).doesNotExist())
              .andDo(this.documentationHandler.document(
                      requestHeaders(
                              headerWithName(AUTHORIZATION).description("유저의 AccessToken")
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