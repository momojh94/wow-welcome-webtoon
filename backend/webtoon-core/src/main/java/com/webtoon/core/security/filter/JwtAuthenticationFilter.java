package com.webtoon.core.security.filter;

import com.webtoon.core.security.config.SecurityConfig;
import com.webtoon.core.security.provider.JwtTokenProvider;
import com.webtoon.core.security.util.AuthorizationExtractor;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;
import java.util.Arrays;

import static com.webtoon.core.security.config.SecurityConfig.PERMIT_GET_URI;
import static com.webtoon.core.security.config.SecurityConfig.PERMIT_POST_URI;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (HttpMethod.GET.matches(request.getMethod())) {
            return Arrays.stream(PERMIT_GET_URI)
                         .anyMatch(path -> pathMatcher.match(path, request.getServletPath()));
        }

        if (HttpMethod.POST.matches(request.getMethod())) {
            return Arrays.stream(PERMIT_POST_URI)
                         .anyMatch(path -> pathMatcher.match(path, request.getServletPath()));
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (jwtTokenProvider.validateAccessToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthenticationFrom(jwt);
            SecurityContextHolder.getContext()
                                 .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        return AuthorizationExtractor.extract(bearerToken);
    }

}
