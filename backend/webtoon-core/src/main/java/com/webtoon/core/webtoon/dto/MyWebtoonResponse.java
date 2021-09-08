package com.webtoon.core.webtoon.dto;

import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyWebtoonResponse {

	private Long idx;
	private String title;
	private String thumbnail;
	private LocalDateTime createdDate;
	private LocalDateTime lastUpdated;

	@Builder
	private MyWebtoonResponse(Long idx, String title, String thumbnail,
							 LocalDateTime createdDate, LocalDateTime lastUpdated) {
		this.idx = idx;
		this.title = title;
		this.thumbnail = thumbnail;
		this.createdDate = createdDate;
		this.lastUpdated = lastUpdated;
	}

	public static MyWebtoonResponse of(Webtoon webtoon) {
		String thumbnail = new StringBuilder().append("http://localhost:8080/static/web_thumbnail/")
											  .append(webtoon.getThumbnail()).toString();
		return MyWebtoonResponse.builder()
								.idx(webtoon.getIdx())
								.title(webtoon.getTitle())
								.thumbnail(thumbnail)
								.createdDate(webtoon.getCreatedDate())
								.lastUpdated(webtoon.getLastUpdatedDate())
								.build();
	}
}
