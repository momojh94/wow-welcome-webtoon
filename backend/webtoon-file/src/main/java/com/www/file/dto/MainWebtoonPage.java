package com.www.file.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MainWebtoonPage {
	private List<MainWebtoonDto> webtoons;
	private int totalPages;
	
	public MainWebtoonPage(List<MainWebtoonDto> webtoons, int totalPages) {
		this.webtoons = webtoons;
		this.totalPages = totalPages;
	}
}
