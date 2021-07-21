package com.webtoon.core.episode.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EpisodePage {
	private List<EpisodeListDto> episodes;
	private int totalPages;
	private String webtoonThumbnail;
	private String webtoonTitle;
	private String plot;
	private String writer;
	private String id;
	
	public EpisodePage(List<EpisodeListDto> episodes, int totalPages) {
		this.episodes = episodes;
		this.totalPages = totalPages;
	}
}
