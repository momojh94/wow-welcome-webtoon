package com.webtoon.core.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.webtoon.core.common.exception.CustomException;
import com.webtoon.core.common.exception.ExceptionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiResponse<T> {

    public static final String SUCCESS = "success";
    public static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<Void>(null, SUCCESS, null);

    private String errorCode;
    private String message;
    private T data;

    private ApiResponse(String errorCode, String message, T data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse<Void> succeed() {
        return SUCCESS_RESPONSE;
    }

    public static <T> ApiResponse<T> succeed(T data) {
        return new ApiResponse<>(null, SUCCESS, data);
    }

    public static <Void> ApiResponse<Void> fail(CustomException exception) {
        return new ApiResponse<Void>(exception.getErrorCode(), exception.getMessage(), null);
    }

    public static <Void> ApiResponse<Void> fail(String errorCode, String message) {
        return new ApiResponse<Void>(errorCode, message, null);
    }

    public static <T> ApiResponse<T> fail(String errorCode, String message, T data) {
        return new ApiResponse<T>(errorCode, message, data);
    }
}
