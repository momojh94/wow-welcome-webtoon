package com.www.core.auth.enums;

import com.www.core.common.enums.EnumAttributeConverter;

public class GenderConverter extends EnumAttributeConverter<Gender, Integer> {
    public GenderConverter() {
        super(Gender.class);
    }
}