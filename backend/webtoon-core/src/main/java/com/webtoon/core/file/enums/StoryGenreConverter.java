package com.webtoon.core.file.enums;

import com.webtoon.core.common.enums.EnumAttributeConverter;

public class StoryGenreConverter extends EnumAttributeConverter<StoryGenre, Integer> {
    public StoryGenreConverter() { super(StoryGenre.class); }
}
