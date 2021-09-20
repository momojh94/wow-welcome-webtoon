package com.webtoon.core.episode.dto;

import com.webtoon.core.episode.domain.Episode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeViewPageResponse {

	private Long idx;
	private int epNo;
	private String title;
	private String thumnail;
	private LocalDateTime createdDate;
	private float ratingAvg;
	
	@Builder
	private EpisodeViewPageResponse(Long idx, int epNo, String title, String thumbnail,
									float ratingAvg, LocalDateTime createdDate) {
		this.idx = idx;
		this.epNo = epNo;
		this.title = title;
		this.thumnail = thumbnail;
		this.ratingAvg = ratingAvg;
		this.createdDate = createdDate;
	}

	public static EpisodeViewPageResponse of(Episode episode) {
		return EpisodeViewPageResponse.builder()
									  .idx(episode.getIdx())
									  .epNo(episode.getEpNo())
									  .title(episode.getTitle())
									  .thumbnail(episode.getThumbnail())
									  .ratingAvg(episode.getRatingAvg())
									  .createdDate(episode.getCreatedDate())
									  .build();
	}
}
