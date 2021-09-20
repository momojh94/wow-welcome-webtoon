package com.webtoon.core.episode.dto;

import com.webtoon.core.common.util.FileUploader;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeDetailResponse {

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
	private EpisodeDetailResponse(String webtoonTitle, String title, String author, String summary,
								  String thumbnail, String authorComment, int ratingPersonTotal,
								  float ratingAvg, int epHits, String[] contents) {
		this.webtoonTitle = webtoonTitle;
		this.title = title;
		this.author = author;
		this.summary = summary;
		this.thumbnail = thumbnail;
		this.authorComment = authorComment;
		this.ratingPersonTotal = ratingPersonTotal;
		this.ratingAvg = ratingAvg;
		this.epHits = epHits;
		this.contents = contents;
	}

	public static EpisodeDetailResponse of(Episode episode) {
		Webtoon webtoon = episode.getWebtoon();
		return EpisodeDetailResponse.builder()
									.webtoonTitle(webtoon.getTitle())
									.title(episode.getTitle())
									.author(webtoon.getAuthor())
									.summary(webtoon.getSummary())
									.thumbnail(FileUploader.webtoonThumbnailStaticResourcePathOf(webtoon.getThumbnail()))
									.authorComment(episode.getAuthorComment())
									.ratingPersonTotal(episode.getRatingPersonTotal())
									.ratingAvg(episode.getRatingAvg())
									.epHits(episode.getHits())
									.contents(FileUploader.contentsStaticResourcePathOf(episode.getContents()))
									.build();
	}
}
