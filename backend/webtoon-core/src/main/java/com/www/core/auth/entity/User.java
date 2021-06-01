package com.www.core.auth.entity;

import com.www.core.auth.enums.Gender;
import com.www.core.common.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@NoArgsConstructor
@Getter
@Entity
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

	@Column(nullable = false, length = 45)
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
