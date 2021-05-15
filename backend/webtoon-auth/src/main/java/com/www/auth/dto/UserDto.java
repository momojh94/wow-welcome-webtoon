package com.www.auth.dto;

import com.www.core.auth.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

/**
 * UserDto : not contains pw
 * @author bjiso
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	private String account;
	private String name;
	private Date birth;
	private Gender gender;
	private String email;
}
