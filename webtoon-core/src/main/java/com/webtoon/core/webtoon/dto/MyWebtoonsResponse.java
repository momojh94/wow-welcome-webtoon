package com.webtoon.core.webtoon.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyWebtoonsResponse {

	private List<MyWebtoonResponse> webtoons;
	private int totalPages;

	@Builder
	private MyWebtoonsResponse(List<MyWebtoonResponse> webtoons, int totalPages) {
		this.webtoons = webtoons;
		this.totalPages = totalPages;
	}

	public static MyWebtoonsResponse of(List<MyWebtoonResponse> webtoons, int totalPages) {
		return MyWebtoonsResponse.builder()
								 .webtoons(webtoons)
								 .totalPages(totalPages)
								 .build();
	}
}
