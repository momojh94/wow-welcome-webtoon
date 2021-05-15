package com.www.auth.dto;

import com.www.core.auth.entity.User;

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
	
	public User toEntity(String e_pw) {
		return User.builder()
				.account(getAccount())
				.name(getName())
				.pw(e_pw)
				.birth(getBirth())
				.gender(getGender())
				.email(getEmail())
				.build();
	}
	
}
