package com.webtoon.core.user.domain;

import com.webtoon.core.user.domain.enums.Gender;
import com.webtoon.core.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;

	@Column(length = 20, nullable = false)
	private String account;

	@Column(length = 64, nullable = false)
	private String pw;

	@Column(length = 12, nullable = false)
	private String name;

	@Column(nullable = false)
	private Date birth;

	@Column(nullable = false)
	private Gender gender;

	@Column(length = 45, nullable = false)
	private String email;

	@Builder
	public User(Long idx, String account, String pw, String name, Date birth, Gender gender, String email) {
		this.idx = idx;
		this.account = account;
		this.pw = pw;
		this.name = name;
		this.birth = birth;
		this.gender = gender;
		this.email = email;
	}

}


