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
import com.webtoon.api.comment.dto.EpisodeStarRatingRequestDto;
import com.webtoon.api.comment.dto.EpisodeStarRatingResponseDto;
import com.webtoon.api.comment.service.StarRatingService;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = StarRatingController.class)
public class StarRatingControllerTest {
    @MockBean
    private StarRatingService starRatingService;

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

    @DisplayName("에피소드 별점 주기(등록)")
    @Test
    void createStarRating() throws Exception {
        //given
        float rating = 3f;
        String requestBody = objectMapper.writeValueAsString(new EpisodeStarRatingRequestDto(rating));
        EpisodeStarRatingResponseDto responseData = EpisodeStarRatingResponseDto.builder()
                                                                                .ratingAvg(3.66667f)
                                                                                .personTotal(3)
                                                                                .build();

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(starRatingService.createStarRating(episode.getIdx(), user.getIdx(), rating))
                .willReturn(responseData);

        //when
        ResultActions result = mockMvc.perform(post("/episodes/{ep_idx}/rating", episode.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath(ERROR_CODE).isEmpty())
              .andExpect(jsonPath(MESSAGE).value(ApiResponse.SUCCESS))
              .andExpect(jsonPath("data.rating_avg").value(responseData.getRatingAvg()))
              .andExpect(jsonPath("data.person_total").value(responseData.getPersonTotal()))
              .andDo(this.documentationHandler.document(
                      requestHeaders(
                              headerWithName(AUTH_HEADER).description("유저의 AccessToken")
                      ),
                      pathParameters(
                              parameterWithName("ep_idx").description("에피소드의 idx")
                      ),
                      requestFields(
                              fieldWithPath("rating").description("등록할 별점")
                      ),
                      responseFields(
                              fieldWithPath(ERROR_CODE).type(String.class).description("에러 코드 (에러 없을 시 null)"),
                              fieldWithPath(MESSAGE).description("응답 메시지"),
                              subsectionWithPath(DATA).description("응답 데이터"),
                              fieldWithPath("data.rating_avg").description("해당 에피소드 별점 평균").type(JsonFieldType.NUMBER),
                              fieldWithPath("data.person_total").description("별점 등록 참여자 수").type(JsonFieldType.NUMBER)
                      )));
    }
}
