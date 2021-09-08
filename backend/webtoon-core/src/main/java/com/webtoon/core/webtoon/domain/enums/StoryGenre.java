package com.webtoon.core.webtoon.domain.enums;

import com.webtoon.core.common.enums.PersistableEnum;
import lombok.Getter;

@Getter
public enum StoryGenre implements PersistableEnum<Integer> {

    DAILY(1),
    GAG(2),
    FANTASY(3),
    ACTION(4),
    DRAMA(5),
    PURE(6),
    EMOTION(7);

    private Integer databaseValue;

    StoryGenre(Integer databaseValue) {
        this.databaseValue = databaseValue;
    };

}