package com.webtoon.api.comment.controller;

import com.webtoon.api.ControllerTest;
import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.episode.domain.Episode;

import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.comment.domain.Comment;
import com.webtoon.core.comment.dto.CommentLikeDislikeCountResponse;
import com.webtoon.core.comment.service.CommentLikeDislikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;


import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CommentLikeDislikeController.class)
class CommentLikeDislikeControllerTest extends ControllerTest {

    @MockBean
    private CommentLikeDislikeService commentLikeDislikeService;

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

    @DisplayName("댓글 좋아요 요청")
    @Test
    void requestCommentLike() throws Exception {
        //given
        CommentLikeDislikeCountResponse responseData =
                new CommentLikeDislikeCountResponse(comment.getLikeCount() + 1);

        given(commentLikeDislikeService.requestLike(user, comment.getIdx())).willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(post("/comments/{cmt_idx}/like", comment.getIdx())
                .header(AUTHORIZATION, TEST_AUTHORIZATION_HEADER)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath("data.count").value(comment.getLikeCount() + 1))
              .andDo(this.documentationHandler.document(
                      requestHeaders(
                              headerWithName(AUTHORIZATION).description("유저의 AccessToken")
                      ),
                      RequestDocumentation.pathParameters(
                              RequestDocumentation.parameterWithName("cmt_idx").description("댓글의 idx")
                      ),
                      responseFields(
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
                              fieldWithPath(MESSAGE).description("응답 메시지"),
                              subsectionWithPath(DATA).description("응답 데이터"),
                              fieldWithPath("data.count").description("좋아요 수").type(JsonFieldType.NUMBER)
                      )));
    }

    @DisplayName("댓글 싫어요 요청")
    @Test
    void requestCommentDislike() throws Exception {
        //given
        CommentLikeDislikeCountResponse responseData =
                new CommentLikeDislikeCountResponse(comment.getDislikeCount() + 1);

        given(commentLikeDislikeService.requestDislike(user, comment.getIdx())).willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(post("/comments/{cmt_idx}/dislike", comment.getIdx())
                .header(AUTHORIZATION, TEST_AUTHORIZATION_HEADER)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath("data.count").value(comment.getDislikeCount() + 1))
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
                              subsectionWithPath(DATA).description("응답 데이터"),
                              fieldWithPath("data.count").description("싫어요 수").type(JsonFieldType.NUMBER)
                      )));
    }
}
