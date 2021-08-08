package com.webtoon.core.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoModifiedDto {
	private String name;
	private Date birth;
	private int gender;
	private String pw;
}
