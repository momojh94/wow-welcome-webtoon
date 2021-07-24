package com.webtoon.core.episode.dto;

import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeCreateRequest {

    @NotBlank(message = "title을 입력해주세요")
    private String title;

    @NotBlank(message = "authorComment를 입력해주세요")
    private String authorComment;

    public EpisodeCreateRequest(String title, String authorComment) {
        this.title = title;
        this.authorComment = authorComment;
    }

    public Episode toEpisode(int epNo, String thumbnail,
                             String contents, Webtoon webtoon) {
        return Episode.builder()
                      .epNo(epNo)
                      .title(title)
                      .authorComment(authorComment)
                      .thumbnail(thumbnail)
                      .contents(contents)
                      .webtoon(webtoon)
                      .build();
    }
}
