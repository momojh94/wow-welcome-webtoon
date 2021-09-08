package com.webtoon.core.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String toStringDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }
}
