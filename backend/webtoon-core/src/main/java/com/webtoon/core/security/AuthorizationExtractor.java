package com.webtoon.core.security;

import org.springframework.util.StringUtils;

import static com.webtoon.core.common.util.StringUtils.EMPTY_STRING;
import static com.webtoon.core.security.fixture.SecurityFixture.BEARER_PREFIX;

public class AuthorizationExtractor {

    public static String extract(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return EMPTY_STRING;
    }
}
