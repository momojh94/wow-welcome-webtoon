package com.webtoon.core.user.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignupRequest {

    @NotBlank(message = "account를 입력해주세요.")
    private String account;

    @NotBlank(message = "password를 입력해주세요.")
    @Size(min = 8)
    private String password;

    @NotBlank(message = "name을 입력해주세요.")
    private String name;

    @NotNull(message = "birth를 입력해주세요.")
    private Date birth;

    @NotNull(message = "gender를 입력해주세요.")
    private Gender gender;

    @Email(message = "올바른 email이 필요합니다.")
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
