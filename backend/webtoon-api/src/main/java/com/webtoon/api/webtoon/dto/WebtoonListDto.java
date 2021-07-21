package com.webtoon.api.webtoon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WebtoonListDto {
	private Long idx;
	private String title;
	private String thumbnail;
	private LocalDateTime createdDate;
	private LocalDateTime lastUpdated;
	
	@Builder
	public WebtoonListDto(Long idx, String title, String thumbnail, LocalDateTime createdDate, LocalDateTime lastUpdated) {
		this.idx = idx;
		this.title = title;
		this.thumbnail = thumbnail;
		this.createdDate = createdDate;
		this.lastUpdated = lastUpdated;
	}
}
