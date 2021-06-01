package com.www.core.file.entity;

import com.www.core.common.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Episode extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;

	@Column
	private int epNo;

	@Column
	private String title;

	@Column
	@Type(type = "text")
	private String authorComment;

	@Column
	@Type(type = "text")
	private String thumbnail;

	@Column
	@Type(type = "text")
	private String contents;

	@Column
	private int hits;

	@Column
	private float ratingAvg;

	@Column
	private int ratingPersonTotal;

	@ManyToOne
	@JoinColumn(name = "webtoon_idx")
	private Webtoon webtoon;
	
	@Builder
	public Episode(Long idx, Webtoon webtoon, String title, int epNo, String authorComment,
				   String thumbnail, String contents, int hits, float ratingAvg, int ratingPersonTotal) {
		this.idx = idx;
		this.webtoon = webtoon;
		this.title = title;
		this.epNo = epNo;
		this.authorComment = authorComment;
		this.thumbnail = thumbnail;
		this.contents = contents;
		this.hits = hits;
		this.ratingAvg = ratingAvg;
		this.ratingPersonTotal = ratingPersonTotal;
	}
}
