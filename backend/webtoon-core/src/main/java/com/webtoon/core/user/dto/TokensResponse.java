package com.webtoon.core.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokensResponse {
	private static final String BEARER = "bearer ";
	private String accessToken;
	private String refreshToken;

	@Builder
	private TokensResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public static TokensResponse of(String accessTokenSrc, String refreshToken) {
		String accessToken = new StringBuilder(BEARER).append(accessTokenSrc)
													  .toString();
		return TokensResponse.builder()
							 .accessToken(accessToken)
							 .refreshToken(refreshToken)
							 .build();
	}
}
