package com.webtoon.core.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.webtoon.core.common.exception.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiResponse<T> {

    public static final String SUCCESS = "success";

    private String errorCode;
    private String message;
    private T data;

    private ApiResponse(String errorCode, String message, T data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public static <Void> ApiResponse<Void> succeed() {
        return new ApiResponse<Void>(null, SUCCESS, null);
    }

    public static <T> ApiResponse<T> succeed(T data) {
        return new ApiResponse<>(null, SUCCESS, data);
    }

    public static <Void> ApiResponse<Void> fail(ErrorType errorType) {
        return new ApiResponse<>(errorType.getCode(), errorType.getMessage(), null);
    }

    public static <Void> ApiResponse<Void> fail(ErrorType errorType, String errors) {
        return new ApiResponse<>(errorType.getCode(), errors, null);
    }

    public static <Void> ApiResponse<Void> fail(String errorCode, String message) {
        return new ApiResponse<Void>(errorCode, message, null);
    }
}
