package com.webtoon.core.webtoon.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonCreateRequest {

	@NotBlank(message = "title을 입력해주세요")
	private String title;

	@NotNull(message = "storyType을 입력해주세요")
	private StoryType storyType;

	@NotNull(message = "storyGenre을 입력해주세요")
	private StoryGenre storyGenre;

	@NotBlank(message = "summary을 입력해주세요")
	private String summary;

	@NotBlank(message = "plot을 입력해주세요")
	private String plot;
	private EndFlag endFlag;

	public WebtoonCreateRequest(String title, StoryType storyType, StoryGenre storyGenre,
								String summary, String plot, EndFlag endFlag) {
		this.title = title;
		this.storyType = storyType;
		this.storyGenre = storyGenre;
		this.summary = summary;
		this.plot = plot;
		this.endFlag = endFlag;
	}

	public Webtoon toWebtoon(String thumbnail, User user) {
		return Webtoon.builder()
					  .title(title)
					  .storyType(storyType)
					  .storyGenre(storyGenre)
					  .summary(summary)
					  .plot(plot)
					  .thumbnail(thumbnail)
					  .endFlag(endFlag)
					  .user(user)
					  .build();
	}
}
