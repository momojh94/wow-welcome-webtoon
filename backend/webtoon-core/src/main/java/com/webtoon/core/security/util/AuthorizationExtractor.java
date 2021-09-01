package com.webtoon.core.security.util;

import org.springframework.util.StringUtils;

import static com.webtoon.core.common.util.StringUtils.EMPTY_STRING;

public class AuthorizationExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    public static String extract(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return EMPTY_STRING;
    }
}
