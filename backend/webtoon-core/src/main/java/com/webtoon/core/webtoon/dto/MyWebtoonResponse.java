package com.webtoon.core.webtoon.dto;

import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
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

	public MyWebtoonResponse(Webtoon webtoon) {
		this.idx = webtoon.getIdx();
		this.title = webtoon.getTitle();
		// TODO : port랑 기본 path yml설정 변수로 빼기
		this.thumbnail = new StringBuilder().append("http://localhost:8080/static/web_thumbnail/")
											.append(webtoon.getThumbnail()).toString();
		this.createdDate = webtoon.getCreatedDate();
		this.lastUpdated = webtoon.getLastUpdatedDate();
	}
}
