package com.webtoon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.core.security.filter.JwtAuthenticationFilter;
import com.webtoon.core.security.provider.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(RestDocumentationExtension.class)
public abstract class ControllerTest {

    protected static final String TEST_ACCESS_TOKEN_SECRET_KEY = "accesstokensecretkeyforlcoaltestauth";
    protected static final String TEST_REFRESH_TOKEN_SECRET_KEY = "refreshtokensecretkeyforlcoaltestauth";

    protected static final String TEST_AUTHORIZATION_HEADER =
            "Bearer eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWR4IjoxLCJpYXQiOjE2MTk0Mzk0MDcsImV4cCI6MTYyMTIzOTQwN30.Kdqwi7_pw_0VB-nwT4IZNKEYTg2_4Bh3JwFldhd7GLY";

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    protected RestDocumentationResultHandler documentationHandler;
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    protected static final String ERROR_CODE = "error_code";
    protected static final String MESSAGE = "message";
    protected static final String DATA = "data";

    @BeforeEach
    protected void setUp(WebApplicationContext webApplicationContext,
                         RestDocumentationContextProvider restDocumentation) {
        JwtAuthenticationFilter jwtAuthenticationFilter = (JwtAuthenticationFilter)webApplicationContext
                .getBean("jwtAuthenticationFilter");

        documentationHandler = document("{class-name}/{method-name}",
                preprocessRequest(prettyPrint(), modifyUris().removePort()),
                preprocessResponse(prettyPrint()));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                 .addFilter(new CharacterEncodingFilter("UTF-8", true))
                                 .addFilter(jwtAuthenticationFilter)
                                 .apply(documentationConfiguration(restDocumentation))
                                 .alwaysDo(print())
                                 .build();

        objectMapper = new ObjectMapper();
    }
}
