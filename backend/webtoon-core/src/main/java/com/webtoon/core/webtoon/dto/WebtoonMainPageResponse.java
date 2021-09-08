package com.webtoon.core.webtoon.dto;

import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonMainPageResponse {

	private Long idx;
	private String title;
	private String author;
	private String thumbnail;
	private StoryGenre storyGenre;
	private Long hits;
	private float ratingAvg;

	@Builder
	private WebtoonMainPageResponse(Long idx, String title, String author, String thumbnail,
									StoryGenre storyGenre, Long hits, float ratingAvg) {
		this.idx = idx;
		this.title = title;
		this.author = author;
		this.thumbnail = thumbnail;
		this.storyGenre = storyGenre;
		this.hits = hits;
		this.ratingAvg = ratingAvg;
	}

	public static WebtoonMainPageResponse of(Webtoon webtoon) {
		String thumbnail = new StringBuilder().append("http://localhost:8080/static/web_thumbnail/")
											  .append(webtoon.getThumbnail()).toString();

		return WebtoonMainPageResponse.builder()
									  .idx(webtoon.getIdx())
									  .title(webtoon.getTitle())
									  .author(webtoon.getAuthor())
									  .thumbnail(webtoon.getThumbnail())
									  .storyGenre(webtoon.getStoryGenre())
									  .hits(webtoon.getHits())
									  .ratingAvg(webtoon.getRatingAvg())
									  .build();
	}
}
