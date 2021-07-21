package com.webtoon.core.webtoon.domain.enums;

import com.webtoon.core.common.enums.PersistableEnum;
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
