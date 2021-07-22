package com.webtoon.core.webtoon.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyWebtoonsResponse {
	private List<MyWebtoonResponse> webtoons;
	private int totalPages;
	
	public MyWebtoonsResponse(List<MyWebtoonResponse> webtoons, int totalPages) {
		this.webtoons = webtoons;
		this.totalPages = totalPages;
	}
}
