package com.www.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.www.core.auth.Gender;
import com.www.core.auth.entity.User;
import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.core.file.entity.Episode;
import com.www.core.file.entity.Webtoon;
import com.www.core.platform.entity.Comment;
import com.www.platform.dto.*;
import com.www.platform.service.StarRatingService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
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
                .toonType((byte) 0)
                .genre1((byte) 0)
                .genre2((byte) 0)
                .summary("웹툰 한줄 요약")
                .plot("줄거리")
                .thumbnail("thumbnailTest.jpg")
                .endFlag((byte) 0)
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
    void insertStarRating() throws Exception {
        //given
        StarRatingRequestDto dto = new StarRatingRequestDto(5f);
        String requestBody = objectMapper.writeValueAsString(dto);
        EpisodeStarRatingResponseDto responseData =
                EpisodeStarRatingResponseDto.builder()
                        .rating(3.66667f)
                        .personTotal(3)
                        .build();

        Response<EpisodeStarRatingResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : insert star rating");
        response.setData(responseData);

        given(tokenChecker.validateToken(ACCESS_TOKEN)).willReturn(0);
        given(tokenChecker.getUserIdx(ACCESS_TOKEN)).willReturn(user.getIdx());
        given(starRatingService.insertStarRating(episode.getIdx(), user.getIdx(), dto.getRating()))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(post("/episodes/{ep_idx}/rating", episode.getIdx())
                .header(AUTH_HEADER, ACCESS_TOKEN)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath(CODE).value(0))
                .andExpect(jsonPath(MESSAGE).value("request complete : insert star rating"))
                .andExpect(jsonPath("data.rating").value(responseData.getRating()))
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
                                fieldWithPath(CODE).description("응답 코드"),
                                fieldWithPath(MESSAGE).description("응답 메시지"),
                                subsectionWithPath(DATA).description("응답 데이터"),
                                fieldWithPath("data.rating").description("해당 에피소드 별점 평균").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.person_total").description("별점 등록 참여자 수").type(JsonFieldType.NUMBER)
                        )));
    }
}
