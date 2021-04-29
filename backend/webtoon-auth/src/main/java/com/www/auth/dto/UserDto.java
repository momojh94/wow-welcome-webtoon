package com.www.auth.dto;

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

//controller dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	//private Long idx;
	private String userid;
	private String name;
	private Date birth;
	private byte gender;
	private String email;

}
