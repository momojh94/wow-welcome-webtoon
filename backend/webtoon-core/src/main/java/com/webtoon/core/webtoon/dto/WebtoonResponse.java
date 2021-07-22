package com.webtoon.core.webtoon.dto;

import com.webtoon.core.webtoon.domain.Webtoon;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebtoonResponse {

    private String title;
    private StoryType storyType;
    private StoryGenre storyGenre1;
    private StoryGenre storyGenre2;
    private String summary;
    private String plot;
    private String thumbnail;
    private EndFlag endFlag;

    public WebtoonResponse(Webtoon webtoon) {
        this.title = webtoon.getTitle();
        this.storyType = webtoon.getStoryType();
        this.storyGenre1 = webtoon.getStoryGenre1();
        this.storyGenre2 = webtoon.getStoryGenre2();
        this.summary = webtoon.getSummary();
        this.plot = webtoon.getPlot();
        this.thumbnail = webtoon.getThumbnail();
        this.endFlag = webtoon.getEndFlag();
    }
}