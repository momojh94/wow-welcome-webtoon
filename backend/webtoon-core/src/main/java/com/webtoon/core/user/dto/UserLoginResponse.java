package com.webtoon.core.user.dto;

import com.webtoon.core.user.domain.enums.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginResponse {
    private String account;
    private String name;
    private Date birth;
    private Gender gender;
    private String email;
    private String token;

    @Builder
    private UserLoginResponse(String account, String name, Date birth, Gender gender,
                              String email, String token) {
        this.account = account;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
        this.token = token;
    }

    public static UserLoginResponse of(UserResponse userResponse,
                                       com.webtoon.core.security.dto.UserLoginResponse userLoginResponse) {
        return com.webtoon.core.user.dto.UserLoginResponse.builder()
                                                          .account(userResponse.getAccount())
                                                          .name(userResponse.getName())
                                                          .birth(userResponse.getBirth())
                                                          .gender(userResponse.getGender())
                                                          .email(userResponse.getEmail())
                                                          .token(userLoginResponse.getRefreshToken())
                                                          .build();
    }
}
