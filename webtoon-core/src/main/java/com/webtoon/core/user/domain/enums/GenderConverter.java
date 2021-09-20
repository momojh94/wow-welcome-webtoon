package com.webtoon.core.user.domain.enums;

import com.webtoon.core.common.enums.EnumAttributeConverter;

public class GenderConverter extends EnumAttributeConverter<Gender, Integer> {

    public GenderConverter() {
        super(Gender.class);
    }

}