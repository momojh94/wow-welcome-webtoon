package com.webtoon.api.webtoon.dto;

import com.webtoon.core.user.domain.User;

import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebtoonDto {
	private String title;
	private StoryType storyType;
	private StoryGenre storyGenre1;
	private StoryGenre storyGenre2;
	private String summary;
	private String plot;
	private String thumbnail;
	private EndFlag endFlag;
	private User user;

	public WebtoonDto(String title, StoryType storyType, StoryGenre storyGenre1, StoryGenre storyGenre2,
					  String summary, String plot, EndFlag endFlag) {
		this.title = title;
		this.storyType = storyType;
		this.storyGenre1 = storyGenre1;
		this.storyGenre2 = storyGenre2;
		this.summary = summary;
		this.plot = plot;
		this.endFlag = endFlag;
	}

	public Webtoon toEntity() {
		Webtoon build = Webtoon.builder()
				.title(title)
				.storyType(storyType)
				.storyGenre1(storyGenre1)
				.storyGenre2(storyGenre2)
				.summary(summary)
				.plot(plot)
				.thumbnail(thumbnail)
				.endFlag(endFlag)
				.build();
		return build;
	}
	
	@Builder
	public WebtoonDto(String title, StoryType storyType, StoryGenre storyGenre1, StoryGenre storyGenre2, String summary, String plot,
					  String thumbnail, EndFlag endFlag) {
		this.title = title;
		this.storyType = storyType;
		this.storyGenre1 = storyGenre1;
		this.storyGenre2 = storyGenre2;
		this.summary = summary;
		this.plot = plot;
		this.thumbnail = thumbnail;
		this.endFlag = endFlag;
	}
}
