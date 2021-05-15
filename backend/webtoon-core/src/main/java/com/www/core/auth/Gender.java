package com.www.core.auth;

import com.www.core.common.EntityEnumAttribute;
import lombok.Getter;

@Getter
public enum Gender implements EntityEnumAttribute {
    MALE("남성", 1),
    FEMALE("여성", 2);

    private String codeName;
    private byte codeNumber;

    Gender(String codeName, int codeNumber) {
        this.codeName = codeName;
        this.codeNumber = (byte) codeNumber;
    }
}
