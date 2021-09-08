package com.webtoon.core.webtoon.dto;

import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonEditRequest {

    @NotBlank
    private String title;

    @NotNull
    private StoryType storyType;

    @NotNull
    private StoryGenre storyGenre;

    @NotBlank
    private String summary;

    @NotBlank
    private String plot;

    @NotNull
    private EndFlag endFlag;

    public WebtoonEditRequest(String title, StoryType storyType, StoryGenre storyGenre,
                              String summary, String plot, EndFlag endFlag) {
        this.title = title;
        this.storyType = storyType;
        this.storyGenre = storyGenre;
        this.summary = summary;
        this.plot = plot;
        this.endFlag = endFlag;
    }

    public Webtoon toWebtoon(String thumbnail) {
        return Webtoon.builder()
                      .title(title)
                      .storyType(storyType)
                      .storyGenre(storyGenre)
                      .summary(summary)
                      .plot(plot)
                      .thumbnail(thumbnail)
                      .endFlag(endFlag)
                      .build();
    }
}