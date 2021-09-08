package com.webtoon.core.episode.dto;

import com.webtoon.core.episode.domain.Episode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeResponse {

	private int epNo;
	private String title;
	private String authorComment;
	private String thumbnail;
	private String contents;

	@Builder
	private EpisodeResponse(int epNo, String title, String authorComment,
							String thumbnail, String contents) {
		this.epNo = epNo;
		this.title = title;
		this.authorComment = authorComment;
		this.thumbnail = thumbnail;
		this.contents = contents;
	}

	public static EpisodeResponse of(Episode episode) {
		return EpisodeResponse.builder()
							  .epNo(episode.getEpNo())
							  .title(episode.getTitle())
							  .authorComment(episode.getAuthorComment())
							  .thumbnail(episode.getThumbnail())
							  .contents(episode.getContents())
							  .build();
	}
}
