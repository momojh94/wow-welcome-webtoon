package com.webtoon.core.webtoon.domain.enums;

import com.webtoon.core.common.enums.EnumAttributeConverter;

public class StoryGenreConverter extends EnumAttributeConverter<StoryGenre, Integer> {
    public StoryGenreConverter() { super(StoryGenre.class); }
}
