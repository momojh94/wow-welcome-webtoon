package com.webtoon.core.webtoon.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonsMainPageResponse {

	private List<WebtoonMainPageResponse> webtoons;
	private int totalPages;

	@Builder
	private WebtoonsMainPageResponse(List<WebtoonMainPageResponse> webtoons, int totalPages) {
		this.webtoons = webtoons;
		this.totalPages = totalPages;
	}

	public static WebtoonsMainPageResponse of(List<WebtoonMainPageResponse> webtoons, int totalPages) {
		return WebtoonsMainPageResponse.builder()
									   .webtoons(webtoons)
									   .totalPages(totalPages)
									   .build();
	}
}
