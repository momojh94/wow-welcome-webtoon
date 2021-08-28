package com.webtoon.core.security.provider;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.webtoon.core.common.exception.ErrorType.INVALID_JWT;
import static com.webtoon.core.common.exception.ErrorType.LOGIN_REQUIRED;

@Component
public class JwtTokenProvider {

    @Value("${jwt.access-token.secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.access-token.expire-length}")
    private long ACCESS_TOKEN_EXPIRE_LENGTH;

    @Value("${jwt.refresh-token.secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh-token.expire-length}")
    private long REFRESH_TOKEN_EXPIRE_LENGTH;

    private static final String BEARER = "bearer ";
    private static final String USER_IDX = "userIdx";
    private static final String TYP = "typ";
    private static final String JWT = "jwt";

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    public JwtTokenProvider(RedisTemplate<String, Object> redisTemplate,
                            UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public String createAccessToken(String userIdx) {
        Date now = new Date();
        Claims claims = Jwts.claims();
        claims.put(USER_IDX, userIdx);

        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_LENGTH);
        return Jwts.builder()
                   .setHeaderParam(TYP, JWT)
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(expiration)
                   .signWith(SignatureAlgorithm.HS256, ACCESS_TOKEN_SECRET_KEY)
                   .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_LENGTH);

        return Jwts.builder()
                   .setHeaderParam(TYP, JWT)
                   .setIssuedAt(now)
                   .setExpiration(expiration)
                   .signWith(SignatureAlgorithm.HS256, REFRESH_TOKEN_SECRET_KEY)
                   .compact();
    }


    public Long getUserIdxOf(String accessToken) {
        try {
            accessToken = accessToken.substring(BEARER.length());
            return Jwts.parser()
                       .setSigningKey(ACCESS_TOKEN_SECRET_KEY)
                       .parseClaimsJws(accessToken)
                       .getBody()
                       .get(USER_IDX, Long.class);
        } catch (MalformedJwtException e) {
            throw new ApplicationException(INVALID_JWT);
        }
    }

    public void validateAccessToken(String accessToken) {
        try {
            accessToken = accessToken.substring(BEARER.length());
            Jwts.parser()
                .setSigningKey(ACCESS_TOKEN_SECRET_KEY)
                .parseClaimsJws(accessToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApplicationException(INVALID_JWT);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser()
                .setSigningKey(REFRESH_TOKEN_SECRET_KEY)
                .parseClaimsJws(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApplicationException(LOGIN_REQUIRED);
        }
    }


    /*
    public void expireToken(Long userIdx) {
    }
     */
}
