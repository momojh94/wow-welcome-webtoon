package com.webtoon.core.episode.dto;

import com.webtoon.core.common.util.FileUploader;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodesViewPageResponse {

	private List<EpisodeViewPageResponse> episodes;
	private String webtoonThumbnail;
	private String webtoonTitle;
	private String plot;
	private String author;
	private int totalPages;

	@Builder
	private EpisodesViewPageResponse(List<EpisodeViewPageResponse> episodes, String webtoonThumbnail, String webtoonTitle,
									 String plot, String author, int totalPages) {
		this.episodes = episodes;
		this.webtoonThumbnail = webtoonThumbnail;
		this.webtoonTitle = webtoonTitle;
		this.plot = plot;
		this.author = author;
		this.totalPages = totalPages;
	}

	public static EpisodesViewPageResponse of(List<EpisodeViewPageResponse> episodes,
											  Webtoon webtoon, int totalPages) {
		return EpisodesViewPageResponse.builder()
									   .episodes(episodes)
									   .webtoonThumbnail(FileUploader.webtoonThumbnailStaticResourcePathOf(webtoon.getThumbnail()))
									   .webtoonTitle(webtoon.getTitle())
									   .plot(webtoon.getPlot())
									   .author(webtoon.getAuthor())
									   .totalPages(totalPages)
									   .build();
	}
}
