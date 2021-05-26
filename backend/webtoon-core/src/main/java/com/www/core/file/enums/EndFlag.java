package com.www.core.file.enums;

import com.www.core.common.enums.PersistableEnum;
import lombok.Getter;

@Getter
public enum EndFlag implements PersistableEnum<Integer> {
    COMPLETED(0),
    ONGOING(1);

    private Integer databaseValue;

    EndFlag(Integer databaseValue) {
        this.databaseValue = databaseValue;
    };
}