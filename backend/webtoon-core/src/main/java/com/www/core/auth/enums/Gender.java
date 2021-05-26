package com.www.core.auth.enums;

import com.www.core.common.enums.PersistableEnum;
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
