package com.www.core.file.enums;

import com.www.core.common.enums.PersistableEnum;
import lombok.Getter;

@Getter
public enum StoryType implements PersistableEnum<Integer> {
    EPISODE(1),
    OMNIBUS(2),
    STORY(3);

    private Integer databaseValue;

    StoryType(Integer databaseValue) {
        this.databaseValue = databaseValue;
    };
}
