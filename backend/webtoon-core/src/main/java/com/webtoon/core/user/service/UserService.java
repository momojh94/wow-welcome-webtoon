package com.webtoon.core.user.service;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.UserRepository;
import com.webtoon.core.user.dto.TokensResponse;
import com.webtoon.core.user.dto.UserSignupRequest;
import com.webtoon.core.user.dto.UserUpdateRequest;
import com.webtoon.core.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.webtoon.core.common.exception.ErrorType.ALREADY_JOINED_ACCOUNT;
import static com.webtoon.core.common.exception.ErrorType.USER_NOT_FOUND;
import static com.webtoon.core.common.exception.ErrorType.WRONG_PASSWORD;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public UserService(UserRepository userRepository,
					   PasswordEncoder passwordEncoder,
					   JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public UserResponse findByAccount(String account) {
		User user = userRepository.findByAccount(account)
								  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

		return UserResponse.of(user);
	}

	public TokensResponse login(String account, String password) {
		User user = userRepository.findByAccount(account)
								  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

		if (!passwordEncoder.matches(password, user.getPw())) {
			throw new ApplicationException(WRONG_PASSWORD);
		}

		user.updateLoginDate();

		return TokensResponse.of(jwtService.createAccessToken(user.getIdx(), user.getName()),
				jwtService.createRefreshToken(user.getAccount()));
	}

	public void signup(UserSignupRequest request) {
		if (userRepository.existsByAccount(request.getAccount())) {
			throw new ApplicationException(ALREADY_JOINED_ACCOUNT);
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		userRepository.save(request.toUser(encodedPassword));
	}

	public void update(Long userIdx, UserUpdateRequest request) {
		User user = userRepository.findById(userIdx)
								  .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));

		String encodedPassword = passwordEncoder.encode(request.getPw());
		user.update(request.toUser(encodedPassword));
	}

	public void delete(Long userIdx) {
		userRepository.deleteById(userIdx);
	}
}
