package com.webtoon.core.episode.domain;

import com.webtoon.core.common.BaseTimeEntity;
import com.webtoon.core.webtoon.domain.Webtoon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

	public void increaseHits() {
		this.hits++;
	}

	public Episode update(Episode requestEpisode) {
		this.title = requestEpisode.title;
		this.title = requestEpisode.title;
		this.epNo = requestEpisode.epNo;
		this.authorComment = requestEpisode.authorComment;
		this.thumbnail = requestEpisode.thumbnail;
		this.contents = requestEpisode.contents;
		this.webtoon = requestEpisode.webtoon;

		return this;
	}
}
