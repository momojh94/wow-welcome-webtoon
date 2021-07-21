package com.webtoon.core.webtoon.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WebtoonPage {
	private List<WebtoonListDto> webtoons;
	private int totalPages;
	
	public WebtoonPage(List<WebtoonListDto> webtoons, int totalPages) {
		this.webtoons = webtoons;
		this.totalPages = totalPages;
	}
}
