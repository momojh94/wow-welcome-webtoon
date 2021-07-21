package com.webtoon.core.file.enums;

import com.webtoon.core.common.enums.EnumAttributeConverter;

public class StoryTypeConverter extends EnumAttributeConverter<StoryType, Integer> {
    public StoryTypeConverter() { super(StoryType.class); }
}
