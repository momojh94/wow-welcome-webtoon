package com.webtoon.core.user.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUpdateRequest {
	private String pw;
	private String name;
	private Date birth;
	private Gender gender;

	public UserUpdateRequest(String pw, String name, Date birth, Gender gender) {
		this.pw = pw;
		this.name = name;
		this.birth = birth;
		this.gender = gender;
	}

	public User toUser(String encodedPassword) {
		return User.builder()
				   .pw(encodedPassword)
				   .name(name)
				   .birth(birth)
				   .gender(gender)
				   .build();
	}
}
