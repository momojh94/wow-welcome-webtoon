package com.webtoon.core.security.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginResponse {

	private String accessToken;
	private String refreshToken;

	@Builder
	private UserLoginResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public static UserLoginResponse of(String accessToken, String refreshToken) {
		return UserLoginResponse.builder()
								.accessToken(accessToken)
								.refreshToken(refreshToken)
								.build();
	}
}
