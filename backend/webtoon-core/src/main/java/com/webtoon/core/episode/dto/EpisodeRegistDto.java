package com.webtoon.core.episode.dto;
import com.webtoon.core.episode.domain.*;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EpisodeRegistDto extends EpisodeDto {
	private Webtoon webtoon;
	
	public EpisodeRegistDto(EpisodeDto p, Webtoon webtoon) {
		setAuthorComment(p.getAuthorComment());
		setContents(p.getContents());
		setEpNo(p.getEpNo());
		setThumbnail(p.getThumbnail());
		setTitle(p.getTitle());
		this.webtoon = webtoon;
	}

	public Episode toEntity() {
		Episode build = Episode.builder()
				.epNo(getEpNo())
				.title(getTitle())
				.authorComment(getAuthorComment())
				.thumbnail(getThumbnail())
				.contents(getContents())
				.webtoon(webtoon)
				.build();
		return build;
	}
}
