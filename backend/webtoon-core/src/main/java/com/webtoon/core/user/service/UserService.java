package com.webtoon.core.user.service;

import com.webtoon.core.common.exception.ApplicationException;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.repository.UserRepository;
import com.webtoon.core.user.dto.UserSignupRequest;
import com.webtoon.core.user.dto.UserUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.webtoon.core.common.exception.ExceptionType.ALREADY_JOINED_ACCOUNT;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void signup(UserSignupRequest request) {
		if (userRepository.existsByAccount(request.getAccount())) {
			throw new ApplicationException(ALREADY_JOINED_ACCOUNT);
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		userRepository.save(request.toUser(encodedPassword));
	}

	public void update(User user, UserUpdateRequest request) {
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		user.update(request.toUser(encodedPassword));
	}

	public void delete(User user) {
		userRepository.delete(user);
	}
}
