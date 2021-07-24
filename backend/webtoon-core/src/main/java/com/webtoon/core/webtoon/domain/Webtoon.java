package com.webtoon.core.webtoon.domain;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.common.BaseTimeEntity;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Webtoon extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;

	@Column
	private String title;

	@Column
	private StoryType storyType;

	@Column
	private StoryGenre storyGenre1;

	@Column
	private StoryGenre storyGenre2;

	@Column
	private String summary;

	@Column
	@Type(type = "text")
	private String plot;

	@Column
	@Type(type = "text")
	private String thumbnail;

	@Column
	private EndFlag endFlag;

	@Column
	private float ratingAvg;

	@Column
	private Long hits;

	@OneToMany(fetch= FetchType.EAGER, orphanRemoval = true , cascade = CascadeType.REMOVE, mappedBy = "webtoon")
	private List<Episode> episodes = new ArrayList<Episode>();

	@ManyToOne
	@JoinColumn(name = "user_idx")
	private User user;

	@Builder
	public Webtoon(Long idx, String title, StoryType storyType, StoryGenre storyGenre1, StoryGenre storyGenre2,
				   String summary, String plot, String thumbnail, EndFlag endFlag,
				   User user, float ratingAvg) {
		this.idx = idx;
		this.title = title;
		this.storyType = storyType;
		this.storyGenre1 = storyGenre1;
		this.storyGenre2 = storyGenre2;
		this.summary = summary;
		this.plot = plot;
		this.thumbnail = thumbnail;
		this.endFlag = endFlag;
		this.user = user;
		this.ratingAvg = ratingAvg;
		this.hits = 0L;
	}

	public boolean wasDrawnBy(Long userIdx) {
		return this.user.getIdx() == userIdx;
	}

	public LocalDateTime getLastUpdatedDate() {
		if(episodes.isEmpty()) {
			return getCreatedDate();
		}

		return episodes.get(episodes.size() - 1)
					   .getUpdatedDate();
	}

	public int getNewEpNo() {
		if(episodes.isEmpty()) {
			return 1;
		}
		int lastEpNo = episodes.get(episodes.size() - 1)
							   .getEpNo();
		return lastEpNo + 1;
	}

	public String getAuthor() {
		return user.getAccount();
	}

	public void increaseHits() {
		this.hits++;
	}

	public Webtoon update(Webtoon requestWebtoon) {
		this.title = requestWebtoon.title;
		this.storyType = requestWebtoon.storyType;
		this.storyGenre1 = requestWebtoon.storyGenre1;
		this.storyGenre2 = requestWebtoon.storyGenre2;
		this.summary = requestWebtoon.summary;
		this.plot = requestWebtoon.plot;
		this.thumbnail = requestWebtoon.thumbnail;
		this.endFlag = requestWebtoon.endFlag;

		return this;
	}
}


