package com.webtoon.core.security.fixture;

public class SecurityFixture {

    public static final String AUTH_EXCEPTION = "auth_exception";

    public static final String[] PERMIT_GET_URI = {
            "/episode/**",
            "/webtoons/**",
            "/monitoring/**"
    };

    public static final String[] PERMIT_HEAD_URI = {
            "/monitoring/**"
    };

    public static final String[] PERMIT_POST_URI = {
            "/auth/**",
            "/users"
    };

    // jwt related
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String USER_IDX = "userIdx";
    public static final String TYP = "typ";
    public static final String JWT = "jwt";

}
