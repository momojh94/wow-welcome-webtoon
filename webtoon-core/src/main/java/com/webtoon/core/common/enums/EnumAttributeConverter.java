package com.webtoon.core.common.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class EnumAttributeConverter<E extends Enum<E> & PersistableEnum<T>, T> implements AttributeConverter<E, T> {
    private Class<E> enumClass;

    public EnumAttributeConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return EnumConverterUtils.toDatabaseValue(attribute);
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        return EnumConverterUtils.toEntityAttribute(enumClass, dbData);
    }
}
