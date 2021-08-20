package com.webtoon.api.user.controller;

import com.webtoon.api.common.ApiResponse;
import com.webtoon.core.user.dto.TokensResponse;
import com.webtoon.core.user.dto.UserSignupRequest;
import com.webtoon.core.user.dto.UserUpdateRequest;
import com.webtoon.core.user.dto.UserLoginRequest;
import com.webtoon.core.user.dto.UserLoginResponse;
import com.webtoon.core.user.dto.UserResponse;

import com.webtoon.core.user.service.JwtService;
import com.webtoon.core.user.service.UserService;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {
	private final UserService userService;
	private final JwtService jwtService;

	public AuthController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping
	public ApiResponse<Void> signup(@RequestBody UserSignupRequest request) {
		userService.signup(request);
		return ApiResponse.succeed();
	}

	@PostMapping("/token")
	public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest request) {
		UserResponse userResponse = userService.findByAccount(request.getAccount());
		TokensResponse tokensResponse = userService.login(request.getAccount(), request.getPw());

		return ResponseEntity.ok()
							 .header(HttpHeaders.AUTHORIZATION, tokensResponse.getAccessToken())
							 .body(ApiResponse.succeed(UserLoginResponse.of(userResponse, tokensResponse)));
	}

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{idx}/token")
	public ApiResponse<String> logout(@RequestHeader("Authorization") String accessToken,
									  @PathVariable("idx") Long useridx, @RequestBody String data) {
		accessToken = accessToken.substring(7);
		JSONObject body = new JSONObject(data);
		String refreshToken = body.getString("refreshToken");

		int r = jwtService.checkRefreshToken(accessToken, refreshToken, useridx);
		switch (r) {
			case 41: // 이미 로그아웃된 상태
				return ApiResponse.fail("41", "access denied: already logout");
			case 40: // refresh token 파기
			case 43:
				jwtService.expireToken(useridx);
				return ApiResponse.succeed();
			default:
		}

		return ApiResponse.fail("42", "access denied: maybe captured or faked token");
	}

	@PostMapping("/{idx}/token")
	public ApiResponse<Void> reissueToken(@RequestHeader("Authorization") String accessToken,
										  @PathVariable("idx") Long userIdx, @RequestBody String data) {
		// access token bearer split
		accessToken = accessToken.substring(7);
		JSONObject body = new JSONObject(data);
		String refreshToken = body.getString("refreshToken");

		switch (jwtService.checkRefreshToken(accessToken, refreshToken, userIdx)) {
			case 40: // 재발급 (code 0)
				String newAccessToken = jwtService.createAccessToken(userIdx, jwtService.getUserName(accessToken));
				ResponseEntity.ok()
							  .header(HttpHeaders.AUTHORIZATION, newAccessToken);
			case 41:
				return ApiResponse.fail("41", "access denied: already logout");
			default:
		}

		return ApiResponse.fail("42", "access denied: maybe captured or faked token");
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping
	public ApiResponse<String> update(@RequestHeader("Authorization") String accessToken,
									  @RequestBody UserUpdateRequest request) {
		switch (jwtService.validateToken(accessToken)) {
			case 0: // 유효한 토큰
				Long userIdx = jwtService.getUserIdx(accessToken);
				if (userIdx == -1) {
					break;
				}
				userService.update(userIdx, request);
				return ApiResponse.succeed();
			case 1: // 만료된 토큰
				return ApiResponse.fail("44", "access denied : invalid access token");
			default:
		}

		return ApiResponse.fail("42", "access denied : maybe captured or faked token");
	}

	@DeleteMapping("/{idx}")
	public ApiResponse<Void> delete(@RequestHeader("Authorization") String accessToken,
									  @PathVariable("idx") Long userIdx) {
		// access token bearer split
		accessToken = accessToken.substring(7);

		// access token 유효한가
		switch (jwtService.validateToken(accessToken)) {
			case 1:
				return ApiResponse.fail("44", "access denied : expired access token");
			case 0:
				if (userIdx == jwtService.getUserIdx(accessToken)) {
					break;
				}
			case 2:
				return ApiResponse.fail("42", "access denied : maybe captured or faked token");
		}

		//회원정보 삭제
		try {
			userService.delete(userIdx);
		} catch (Exception e) {
			return ApiResponse.fail("4", "delete fail: sql error");
		}

		return ApiResponse.succeed();
	}
}
