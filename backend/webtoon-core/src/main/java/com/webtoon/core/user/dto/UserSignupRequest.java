package com.webtoon.core.user.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignupRequest {
    private String account;
    private String password;
    private String name;
    private Date birth;
    private Gender gender;
    private String email;

    public UserSignupRequest(String account, String password, String name,
                             Date birth, Gender gender, String email) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
    }

    public User toUser(String encodedPassword) {
        return User.builder()
                   .account(account)
                   .name(name)
                   .pw(encodedPassword)
                   .birth(birth)
                   .gender(gender)
                   .email(email)
                   .build();
    }
}
