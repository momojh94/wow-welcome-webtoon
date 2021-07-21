package com.webtoon.core.user.domain.enums;

import com.webtoon.core.common.enums.PersistableEnum;
import lombok.Getter;

@Getter
public enum Gender implements PersistableEnum<Integer> {
    MALE(1),
    FEMALE(2);

    private Integer databaseValue;

    Gender(Integer databaseValue) {
        this.databaseValue = databaseValue;
    }
}
