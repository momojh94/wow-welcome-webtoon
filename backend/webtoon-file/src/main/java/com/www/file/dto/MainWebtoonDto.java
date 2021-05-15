package com.www.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MainWebtoonDto {
	private Long idx;
	private String title;
	private String author;
	private String thumbnail;
	private int genre1;
	private int genre2;
	private Long hits;
	private float epRatingAvg;
	private Integer[] pagelist;
	
	@Builder
	public MainWebtoonDto(Long idx, String author, String title, String thumbnail, int genre1, int genre2, float epRatingAvg, Long hits) {
		this.idx = idx;
		this.title = title;
		this.author = author;
		this.thumbnail = thumbnail;
		this.genre1 = genre1;
		this.genre2 = genre2;
		this.epRatingAvg = epRatingAvg;
		this.hits = hits;
	}
}
