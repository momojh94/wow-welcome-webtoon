package com.webtoon.api.user.controller;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.dto.UserSignupRequest;
import com.webtoon.core.user.dto.UserUpdateRequest;
import com.webtoon.core.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	public ApiResponse<Void> signup(@RequestBody @Valid UserSignupRequest request) {
		userService.signup(request);
		return ApiResponse.succeed();
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping
	public ApiResponse<Void> update(@AuthenticationPrincipal User user,
									@RequestBody @Valid UserUpdateRequest request) {
		userService.update(user, request);
		return ApiResponse.succeed();
	}

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping()
	public ApiResponse<Void> delete(@AuthenticationPrincipal User user) {
		userService.delete(user);
		return ApiResponse.succeed();
	}
}
