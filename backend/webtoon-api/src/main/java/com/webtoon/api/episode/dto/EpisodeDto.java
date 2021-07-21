package com.webtoon.api.episode.dto;

import com.webtoon.core.episode.domain.Episode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EpisodeDto {
	private int epNo;
	private String title;
	private String authorComment;
	private String thumbnail;
	private String contents;

	public EpisodeDto(String title, String authorComment) {
		this.title = title;
		this.authorComment = authorComment;
	}

	public Episode toEntity() {
		Episode build = Episode.builder()
				.epNo(epNo)
				.title(title)
				.authorComment(authorComment)
				.thumbnail(thumbnail)
				.contents(contents)
				.build();
		return build;
	}

	public EpisodeDto(EpisodeDto p) {
		this.epNo = epNo;
		this.title = title;
		this.authorComment = authorComment;
		this.thumbnail = thumbnail;
		this.contents = contents;
	}

	@Builder
	public EpisodeDto(int epNo, String title, String authorComment, String thumbnail,
					  String contents) {
		this.epNo = epNo;
		this.title = title;
		this.authorComment = authorComment;
		this.thumbnail = thumbnail;
		this.contents = contents;
	}
	
	

}
