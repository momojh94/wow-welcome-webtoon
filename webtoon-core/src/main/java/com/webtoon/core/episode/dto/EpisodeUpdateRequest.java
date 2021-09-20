package com.webtoon.core.episode.dto;

import com.webtoon.core.episode.domain.Episode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String authorComment;

    public EpisodeUpdateRequest(String title, String authorComment) {
        this.title = title;
        this.authorComment = authorComment;
    }

    public Episode toEpisode(String thumbnail, String contents) {
        return Episode.builder()
                      .title(title)
                      .authorComment(authorComment)
                      .thumbnail(thumbnail)
                      .contents(contents)
                      .build();
    }
}
