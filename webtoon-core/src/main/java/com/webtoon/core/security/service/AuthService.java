
package com.webtoon.core.security.service;

import com.webtoon.core.security.AuthorizationExtractor;
import com.webtoon.core.security.dto.UserLoginResponse;
import com.webtoon.core.security.enums.TokenStatus;
import com.webtoon.core.security.provider.JwtTokenProvider;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.webtoon.core.common.exception.ExceptionType.ALREADY_LOGOUT;
import static com.webtoon.core.common.exception.ExceptionType.INVALID_TOKEN;
import static com.webtoon.core.common.exception.ExceptionType.LOGIN_REQUIRED;
import static com.webtoon.core.common.exception.ExceptionType.USER_NOT_FOUND;
import static com.webtoon.core.common.exception.ExceptionType.WRONG_PASSWORD;
import static com.webtoon.core.security.enums.TokenStatus.EXPIRED;
import static com.webtoon.core.security.enums.TokenStatus.INVALID;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<Long, String> redisTemplate;

    public AuthService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, RedisTemplate<Long, String> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    public UserLoginResponse login(String account, String password) {
        User user = userRepository.findByAccount(account)
                                  .orElseThrow(USER_NOT_FOUND::getException);

        if (!passwordEncoder.matches(password, user.getPw())) {
            throw WRONG_PASSWORD.getException();
        }

        user.updateLoginDate();

        return UserLoginResponse.of(jwtTokenProvider.createAccessToken(user.getIdx()),
                jwtTokenProvider.createRefreshToken(user.getIdx()));
    }

    public void logout(User user, String refreshToken) {
        TokenStatus refreshTokenStatus = jwtTokenProvider.validateRefreshTokenStatus(refreshToken);

        if (refreshTokenStatus == INVALID) {
            throw INVALID_TOKEN.getException();
        }

        ValueOperations<Long, String> vop = redisTemplate.opsForValue();
        String refreshTokenInRedis = vop.get(user.getIdx());

        if (refreshTokenStatus == EXPIRED || StringUtils.isEmpty(refreshTokenInRedis)) {
            throw ALREADY_LOGOUT.getException();
        }

        jwtTokenProvider.expireToken(user);
    }

    public String reissueAccessToken(String accessToken, String refreshToken) {
        Long userIdx = jwtTokenProvider.getUserIdxOf(AuthorizationExtractor.extract(accessToken));
        TokenStatus refreshTokenStatus = jwtTokenProvider.validateRefreshTokenStatus(refreshToken);

        if (refreshTokenStatus == EXPIRED) {
            throw LOGIN_REQUIRED.getException();
        }

        if (refreshTokenStatus == INVALID) {
            throw INVALID_TOKEN.getException();
        }

        ValueOperations<Long, String> vop = redisTemplate.opsForValue();
        String refreshTokenInRedis = vop.get(userIdx);

        if (StringUtils.isEmpty(refreshTokenInRedis)) {
            throw LOGIN_REQUIRED.getException();
        }

        if (!refreshToken.equals(refreshTokenInRedis)) {
            throw INVALID_TOKEN.getException();
        }

        String reissuedAccessToken = jwtTokenProvider.createAccessToken(userIdx);

        return reissuedAccessToken;
    }
}

