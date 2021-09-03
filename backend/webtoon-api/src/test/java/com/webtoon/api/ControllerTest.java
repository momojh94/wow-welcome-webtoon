package com.webtoon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.core.security.filter.JwtAuthenticationFilter;
import com.webtoon.core.security.provider.JwtTokenProvider;
import com.webtoon.core.security.AuthorizationExtractor;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(RestDocumentationExtension.class)
public abstract class ControllerTest {

    protected static final String ERROR_CODE = "error_code";
    protected static final String MESSAGE = "message";
    protected static final String DATA = "data";

    protected static final String TEST_AUTHORIZATION_HEADER =
            "Bearer eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWR4IjoxLCJpYXQiOjE2MzA1MDM2MzUsImV4cCI6MTYzMDUwNTQzNX0.jG7FBq9CBJIVtm_xtV_8FFzm49GzAAd-wU_bxx2RotQ";

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    protected RestDocumentationResultHandler documentationHandler;
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    protected User user;

    @BeforeEach
    protected void setUp(WebApplicationContext webApplicationContext,
                         RestDocumentationContextProvider restDocumentation) throws ParseException {
        JwtAuthenticationFilter jwtAuthenticationFilter = (JwtAuthenticationFilter)webApplicationContext
                .getBean("jwtAuthenticationFilter");

        documentationHandler = document("{class-name}/{method-name}",
                preprocessRequest(prettyPrint(), modifyUris().removePort()),
                preprocessResponse(prettyPrint()));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                 .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                                 .addFilter(jwtAuthenticationFilter)
                                 .apply(documentationConfiguration(restDocumentation))
                                 .alwaysDo(print())
                                 .build();

        objectMapper = new ObjectMapper();

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date birth = simpleDateFormat.parse("1998-03-03");

        user = User.builder()
                   .idx(1L)
                   .account("id123")
                   .name("철수")
                   .pw("1q2w3e4r")
                   .birth(birth)
                   .gender(Gender.MALE)
                   .email("test@email.com")
                   .build();

        String accessToken = AuthorizationExtractor.extract(TEST_AUTHORIZATION_HEADER);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                null, Collections.emptyList());

        given(jwtTokenProvider.validateAccessToken(accessToken)).willReturn(true);
        given(jwtTokenProvider.getAuthenticationFrom(accessToken)).willReturn(authentication);
    }
}
