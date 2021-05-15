package com.www.core.common;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;

public class EnumAttributeConverter<E extends Enum<E> & EntityEnumAttribute> implements AttributeConverter<E, Byte> {
    private Class<E> enumClass;

    @Override
    public Byte convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return 0;
        }

        return attribute.getCodeNumber();
    }

    @Override
    public E convertToEntityAttribute(Byte dbData) {
        System.out.print(dbData);
        System.out.print(enumClass);
        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getCodeNumber() == dbData)
                .findAny()
                .orElse(null);
    }
}
