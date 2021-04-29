package com.www.file.dto;

import com.www.core.auth.entity.Users;
import com.www.core.file.entity.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebtoonDto {
	//private Long idx;
	//private int users_idx;
	private String title;
	private byte toon_type;
	private byte genre1;
	private byte genre2;
	private String summary;
	private String plot;
	private String thumbnail;
	private byte end_flag;
	//private LocalDateTime created_date;
	//private LocalDateTime updated_date;
	//private List<Episode> episodes;
	private Users users;
	public WebtoonDto(String title, byte toon_type, byte genre1, byte genre2, String summary, String plot, byte end_flag) {
		this.title = title;
		this.toon_type = toon_type;
		this.genre1 = genre1;
		this.genre2 = genre2;
		this.summary = summary;
		this.plot = plot;
		this.end_flag = end_flag;
	}
	public Webtoon toEntity() {
		Webtoon build = Webtoon.builder()
				.title(title)
				.toonType(toon_type)
				.genre1(genre1)
				.genre2(genre2)
				.summary(summary)
				.plot(plot)
				.thumbnail(thumbnail)
				.endFlag(end_flag)
				.build();
		return build;
	}
	
	@Builder
	public WebtoonDto(String title, byte toon_type, byte genre1, byte genre2, String summary, String plot,
					  String thumbnail, byte end_flag) {
		this.title = title;
		this.toon_type = toon_type;
		this.genre1 = genre1;
		this.genre2 = genre2;
		this.summary = summary;
		this.plot = plot;
		this.thumbnail = thumbnail;
		this.end_flag = end_flag;
	}
	

}
