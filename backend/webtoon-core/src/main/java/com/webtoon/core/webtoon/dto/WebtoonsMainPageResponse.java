package com.webtoon.core.webtoon.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonsMainPageResponse {
	private List<WebtoonMainPageResponse> webtoons;
	private int totalPages;

	public WebtoonsMainPageResponse(List<WebtoonMainPageResponse> webtoons, int totalPages) {
		this.webtoons = webtoons;
		this.totalPages = totalPages;
	}
}
