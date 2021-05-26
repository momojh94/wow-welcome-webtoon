package com.www.core.file.enums;

import com.www.core.common.enums.PersistableEnum;
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