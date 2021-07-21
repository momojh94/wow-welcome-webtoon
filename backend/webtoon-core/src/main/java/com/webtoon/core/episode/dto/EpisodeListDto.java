package com.webtoon.core.episode.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EpisodeListDto {
	private Long idx;
	private int epNo;
	private String title;
	private String thumnail;
	private String authorComment;
	private LocalDateTime createdDate;
	private float ratingAvg;
	
	@Builder 
	public EpisodeListDto(Long idx, int epNo, String title, String thumbnail,
						  String authorComment, float ratingAvg, LocalDateTime createdDate) {
		this.idx = idx;
		this.epNo = epNo;
		this.title = title;
		this.thumnail = thumbnail;
		this.authorComment = authorComment;
		this.ratingAvg = ratingAvg;
		this.createdDate = createdDate;
	}
}
