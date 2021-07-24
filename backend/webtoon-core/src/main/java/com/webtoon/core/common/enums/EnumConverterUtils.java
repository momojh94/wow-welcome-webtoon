package com.webtoon.core.common.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumConverterUtils {
    public static <E extends Enum<E> & PersistableEnum<T>, T> T toDatabaseValue(E attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException();
        }

        return attribute.getDatabaseValue();
    }

    public static <E extends Enum<E> & PersistableEnum<T>, T> E toEntityAttribute(Class<E> enumClass, T dbData) {
        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getDatabaseValue() == dbData)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown " + dbData));
    }
}
