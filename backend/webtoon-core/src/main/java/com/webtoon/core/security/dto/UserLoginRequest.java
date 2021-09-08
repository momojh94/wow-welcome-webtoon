package com.webtoon.core.security.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginRequest {

	@NotBlank
	private String account;

	@NotBlank
	private String password;

}
