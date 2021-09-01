
package com.webtoon.core.security.service;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.security.enums.TokenStatus;
import com.webtoon.core.security.provider.JwtTokenProvider;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.security.dto.UserLoginResponse;
import com.webtoon.core.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.webtoon.core.common.exception.ErrorType.ALREADY_LOGOUT;
import static com.webtoon.core.common.exception.ErrorType.INVALID_TOKEN;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.WRONG_PASSWORD;
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
                                  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPw())) {
            throw new ApplicationException(WRONG_PASSWORD);
        }

        user.updateLoginDate();

        return UserLoginResponse.of(jwtTokenProvider.createAccessToken(user.getIdx()),
                jwtTokenProvider.createRefreshToken(user.getIdx()));
    }

    public void logout(User user, String refreshToken) {
        TokenStatus refreshTokenStatus = jwtTokenProvider.validateRefreshTokenStatus(refreshToken);

        if (refreshTokenStatus == INVALID) {
            throw new ApplicationException(INVALID_TOKEN);
        }

        ValueOperations<Long, String> vop = redisTemplate.opsForValue();
        String refreshTokenInRedis = vop.get(user.getIdx());

        if (refreshTokenStatus == EXPIRED || StringUtils.isEmpty(refreshTokenInRedis)) {
            throw new ApplicationException(ALREADY_LOGOUT);
        }

        jwtTokenProvider.expireToken(user);
    }

}

