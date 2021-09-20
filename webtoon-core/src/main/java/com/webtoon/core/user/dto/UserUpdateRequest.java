package com.webtoon.core.user.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUpdateRequest {

	@NotBlank
	@Size(min = 8, max = 20)
	private String password;

	@NotBlank
	private String name;

	@NotNull
	private Date birth;

	@NotNull
	private Gender gender;

	public UserUpdateRequest(String password, String name, Date birth, Gender gender) {
		this.password = password;
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
