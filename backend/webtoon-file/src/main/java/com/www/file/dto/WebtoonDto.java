package com.www.file.dto;

import com.www.core.auth.entity.User;
import com.www.core.file.entity.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebtoonDto {
	private String title;
	private byte toonType;
	private byte genre1;
	private byte genre2;
	private String summary;
	private String plot;
	private String thumbnail;
	private byte endFlag;
	private User user;

	public WebtoonDto(String title, byte toonType, byte genre1, byte genre2,
					  String summary, String plot, byte endFlag) {
		this.title = title;
		this.toonType = toonType;
		this.genre1 = genre1;
		this.genre2 = genre2;
		this.summary = summary;
		this.plot = plot;
		this.endFlag = endFlag;
	}

	public Webtoon toEntity() {
		Webtoon build = Webtoon.builder()
				.title(title)
				.toonType(toonType)
				.genre1(genre1)
				.genre2(genre2)
				.summary(summary)
				.plot(plot)
				.thumbnail(thumbnail)
				.endFlag(endFlag)
				.build();
		return build;
	}
	
	@Builder
	public WebtoonDto(String title, byte toonType, byte genre1, byte genre2, String summary, String plot,
					  String thumbnail, byte endFlag) {
		this.title = title;
		this.toonType = toonType;
		this.genre1 = genre1;
		this.genre2 = genre2;
		this.summary = summary;
		this.plot = plot;
		this.thumbnail = thumbnail;
		this.endFlag = endFlag;
	}
}
