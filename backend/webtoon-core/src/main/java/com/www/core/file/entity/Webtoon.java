package com.www.core.file.entity;

import com.www.core.auth.entity.Users;
import com.www.core.common.BaseTimeEntity;
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
	private byte toonType;

	@Column
	private byte genre1;

	@Column
	private byte genre2;

	@Column
	private String summary;

	@Column
	@Type(type = "text")
	private String plot;

	@Column
	@Type(type = "text")
	private String thumbnail;

	@Column
	private byte endFlag;

	@Column
	private float ratingAvg;

	@Column 
	private Long hits;

	@OneToMany(fetch=FetchType.EAGER,  orphanRemoval = true , cascade = CascadeType.REMOVE, mappedBy = "webtoon")
	private List<Episode> episodes = new ArrayList<Episode>();
	
	@ManyToOne
	@JoinColumn(name = "users_idx")
	private Users user;
	
	@Builder
	public Webtoon(Long idx, String title, byte toonType, byte genre1, byte genre2,
				   String summary, String plot, String thumbnail, byte endFlag,
				   Users user, float ratingAvg) {
		this.idx = idx;
		this.title = title;
		this.toonType = toonType;
		this.genre1 = genre1;
		this.genre2 = genre2;
		this.summary = summary;
		this.plot = plot;
		this.thumbnail = thumbnail;
		this.endFlag = endFlag;
		this.user = user;
		this.ratingAvg = ratingAvg;
	}

}
