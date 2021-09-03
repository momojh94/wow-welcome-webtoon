package com.webtoon.core.security;

import org.springframework.util.StringUtils;

import static com.webtoon.core.security.fixture.SecurityFixture.BEARER_PREFIX;
import static org.apache.logging.log4j.util.Strings.EMPTY;

public class AuthorizationExtractor {

    public static String extract(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return EMPTY;
    }
}
