package com.webtoon.core.security.provider;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.common.exception.ErrorType;
import com.webtoon.core.security.enums.TokenStatus;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.webtoon.core.common.exception.ErrorType.EXPIRED_TOKEN;
import static com.webtoon.core.common.exception.ErrorType.INVALID_TOKEN;
import static com.webtoon.core.common.exception.ErrorType.LOGIN_REQUIRED;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;
import static com.webtoon.core.security.enums.TokenStatus.EXPIRED;
import static com.webtoon.core.security.enums.TokenStatus.INVALID;
import static com.webtoon.core.security.enums.TokenStatus.VALID;

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

    private static final String USER_IDX = "userIdx";
    private static final String TYP = "typ";
    private static final String JWT = "jwt";

    private final RedisTemplate<Long, String> redisTemplate;
    private final UserRepository userRepository;

    public JwtTokenProvider(RedisTemplate<Long, String> redisTemplate,
                            UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public String createAccessToken(Long userIdx) {
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

    public String createRefreshToken(Long userIdx) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_LENGTH);

        String refreshToken = Jwts.builder()
                                  .setHeaderParam(TYP, JWT)
                                  .setIssuedAt(now)
                                  .setExpiration(expiration)
                                  .signWith(SignatureAlgorithm.HS256, REFRESH_TOKEN_SECRET_KEY)
                                  .compact();

        ValueOperations<Long, String> vop = redisTemplate.opsForValue();
        vop.set(userIdx, refreshToken);

        redisTemplate.expire(userIdx, REFRESH_TOKEN_EXPIRE_LENGTH, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    public Authentication getAuthenticationFrom(String token) {
        Long userIdx = getUserIdxOf(token);
        User user = userRepository.findById(userIdx)
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public String reissueAccessToken(String accessToken, String refreshToken) {
        TokenStatus accessTokenStatus = validateAccessTokenStatus(accessToken);
        TokenStatus refreshTokenStatus = validateRefreshTokenStatus(refreshToken);

        if (accessTokenStatus == INVALID || refreshTokenStatus == INVALID) {
            throw new ApplicationException(INVALID_TOKEN);
        }

        Long userIdx = getUserIdxOf(accessToken);

        ValueOperations<Long, String> vop = redisTemplate.opsForValue();
        String refreshTokenInRedis = vop.get(userIdx);

        if (refreshTokenStatus == EXPIRED || StringUtils.isEmpty(refreshTokenInRedis)) {
            throw new ApplicationException(LOGIN_REQUIRED);
        }

        String reissuedAccessToken = createAccessToken(userIdx);

        return reissuedAccessToken;
    }

    public void expireToken(User user) {
        redisTemplate.delete(user.getIdx());
    }

    public boolean validateAccessToken(String accessToken) {
        TokenStatus tokenStatus = validateTokenStatus(accessToken, ACCESS_TOKEN_SECRET_KEY);
        if (tokenStatus == INVALID) {
            throw new ApplicationException(INVALID_TOKEN);
        }

        if (tokenStatus == EXPIRED) {
            throw new ApplicationException(EXPIRED_TOKEN);
        }

        return true;
    }

    public TokenStatus validateAccessTokenStatus(String accessToken) {
        return validateTokenStatus(accessToken, ACCESS_TOKEN_SECRET_KEY);
    }

    public TokenStatus validateRefreshTokenStatus(String refreshToken) {
        return validateTokenStatus(refreshToken, REFRESH_TOKEN_SECRET_KEY);
    }

    private TokenStatus validateTokenStatus(String token, String secretKey) {
        if (!StringUtils.hasText(token)) {
            return INVALID;
        }

        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            return VALID;
        } catch (ExpiredJwtException e) {
            return EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return INVALID;
        }
    }

    private Long getUserIdxOf(String accessToken) {
        Long userIdx;
        try {
            userIdx = Jwts.parser()
                          .setSigningKey(ACCESS_TOKEN_SECRET_KEY)
                          .parseClaimsJws(accessToken)
                          .getBody()
                          .get(USER_IDX, Long.class);
        } catch (JwtException e) {
            throw new ApplicationException(ErrorType.INVALID_TOKEN);
        }

        if (userIdx < 0) {
            throw new ApplicationException(ErrorType.INVALID_TOKEN);
        }

        return userIdx;
    }

}
