package com.webtoon.core.webtoon.dto;

import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonMainPageResponse {

	private Long idx;
	private String title;
	private String author;
	private String thumbnail;
	private StoryGenre storyGenre1;
	private StoryGenre storyGenre2;
	private Long hits;
	private float ratingAvg;

	public WebtoonMainPageResponse(Webtoon webtoon) {
		this.idx = webtoon.getIdx();
		this.title = webtoon.getTitle();
		this.author = webtoon.getUser().getAccount();
		// TODO : port랑 기본 path yml설정 변수로 빼기
		this.thumbnail = new StringBuilder().append("http://localhost:8080/static/web_thumbnail/")
											.append(webtoon.getThumbnail()).toString();
		this.storyGenre1 = webtoon.getStoryGenre1();
		this.storyGenre2 = webtoon.getStoryGenre2();
		this.hits = webtoon.getHits();
		this.ratingAvg = webtoon.getRatingAvg();
	}
}
