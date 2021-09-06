package com.webtoon.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.common.exception.ErrorType;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.webtoon.core.security.fixture.SecurityFixture.AUTH_EXCEPTION;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorType errorType = (ErrorType) request.getAttribute(AUTH_EXCEPTION);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(errorType.getStatus());

        String responseBody = objectMapper.writeValueAsString(ApiResponse.fail(errorType));

        response.getWriter().print(responseBody);
    }

}
