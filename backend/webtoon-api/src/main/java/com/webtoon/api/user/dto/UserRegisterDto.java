package com.webtoon.api.user.dto;

import com.webtoon.core.auth.enums.Gender;
import com.webtoon.core.auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserRegisterDto : User input -> UserRegisterDto -> Entity -> DB
 * @author bjiso
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDto extends UserDto{
	private String pw;

	public void setGender(Gender gender) {
		super.gender = gender;
	}
	
	public User toEntity(String ePw) {
		return User.builder()
				.account(getAccount())
				.name(getName())
				.pw(ePw)
				.birth(getBirth())
				.gender(getGender())
				.email(getEmail())
				.build();
	}
}
