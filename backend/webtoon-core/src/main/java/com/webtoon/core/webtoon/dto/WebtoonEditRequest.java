package com.webtoon.core.webtoon.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebtoonEditRequest {

    @NotBlank(message = "title을 입력해주세요")
    private String title;
    private StoryType storyType;
    private StoryGenre storyGenre1;
    private StoryGenre storyGenre2;

    @NotBlank(message = "summary을 입력해주세요")
    private String summary;

    @NotBlank(message = "plot을 입력해주세요")
    private String plot;
    private EndFlag endFlag;

    public WebtoonEditRequest(String title, StoryType storyType, StoryGenre storyGenre1,
                              StoryGenre storyGenre2, String summary, String plot, EndFlag endFlag) {
        this.title = title;
        this.storyType = storyType;
        this.storyGenre1 = storyGenre1;
        this.storyGenre2 = storyGenre2;
        this.summary = summary;
        this.plot = plot;
        this.endFlag = endFlag;
    }

    public Webtoon toEntityWtih(String thumbnail) {
        return Webtoon.builder()
                      .title(title)
                      .storyType(storyType)
                      .storyGenre1(storyGenre1)
                      .storyGenre2(storyGenre2)
                      .summary(summary)
                      .plot(plot)
                      .thumbnail(thumbnail)
                      .endFlag(endFlag)
                      .build();
    }
}