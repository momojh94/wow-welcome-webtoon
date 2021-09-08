package com.webtoon.core.user.dto;

import com.webtoon.core.user.domain.User;
import com.webtoon.core.user.domain.enums.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private String account;
    private String name;
    private Date birth;
    private Gender gender;
    private String email;

    @Builder
    private UserResponse(String account, String name, Date birth, Gender gender,
                        String email) {
        this.account = account;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                           .account(user.getAccount())
                           .name(user.getName())
                           .birth(user.getBirth())
                           .gender(user.getGender())
                           .email(user.getEmail())
                           .build();
    }
}
