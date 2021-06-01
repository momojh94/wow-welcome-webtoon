package com.www.file.dto;

import com.www.core.auth.entity.User;
import com.www.core.file.entity.*;

import com.www.core.file.enums.EndFlag;
import com.www.core.file.enums.StoryGenre;
import com.www.core.file.enums.StoryType;
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
