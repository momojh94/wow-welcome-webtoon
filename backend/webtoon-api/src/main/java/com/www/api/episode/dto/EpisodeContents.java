package com.www.api.episode.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class EpisodeContents {
	private String webtoonTitle;
	private String title;
	private String author;
	private String summary;
	private String thumbnail;
	private String authorComment;
	private int ratingPersonTotal;
	private float ratingAvg;
	private int epHits;
	private String[] contents;
	
	@Builder
	public EpisodeContents(String webtoonTitle, String title, String author, String summary, String thumbnail,
						   String authorComment, int ratingPersonTotal, float ratingAvg, int epHits) {
		this.webtoonTitle = webtoonTitle;
		this.title = title;
		this.authorComment = authorComment;
		this.author = author;
		this.summary = summary;
		this.thumbnail = thumbnail;
		this.ratingPersonTotal = ratingPersonTotal;
		this.ratingAvg = ratingAvg;
		this.epHits = epHits;
	}
}
