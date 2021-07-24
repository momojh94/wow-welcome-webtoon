package com.webtoon.core.common.enums;

public interface PersistableEnum<T> {
    T getDatabaseValue();
}
