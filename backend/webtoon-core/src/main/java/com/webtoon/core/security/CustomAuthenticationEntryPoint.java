package com.webtoon.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;
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
        ExceptionType exceptionType = (ExceptionType) request.getAttribute(AUTH_EXCEPTION);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String responseBody = objectMapper.writeValueAsString(ApiResponse.fail(exceptionType));

        response.getWriter().print(responseBody);
    }

}
