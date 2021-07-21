package com.webtoon.core.webtoon.domain;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.common.BaseTimeEntity;
import com.webtoon.core.episode.domain.Episode;
import com.webtoon.core.webtoon.domain.enums.EndFlag;
import com.webtoon.core.webtoon.domain.enums.StoryGenre;
import com.webtoon.core.webtoon.domain.enums.StoryType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
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

	@OneToMany(fetch=FetchType.EAGER, orphanRemoval = true , cascade = CascadeType.REMOVE, mappedBy = "webtoon")
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
}
