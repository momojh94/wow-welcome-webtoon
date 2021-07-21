package com.webtoon.api.webtoon.dto;

import com.webtoon.core.file.enums.StoryGenre;
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
	private StoryGenre storyGenre1;
	private StoryGenre storyGenre2;
	private Long hits;
	private float ratingAvg;
	private Integer[] pagelist;
	
	@Builder
	public MainWebtoonDto(Long idx, String author, String title, String thumbnail, StoryGenre storyGenre1, StoryGenre storyGenre2, float ratingAvg, Long hits) {
		this.idx = idx;
		this.title = title;
		this.author = author;
		this.thumbnail = thumbnail;
		this.storyGenre1 = storyGenre1;
		this.storyGenre2 = storyGenre2;
		this.ratingAvg = ratingAvg;
		this.hits = hits;
	}
}
