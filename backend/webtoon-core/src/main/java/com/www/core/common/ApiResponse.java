package com.www.core.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private static final String SUCCESS = "success";

    private String code;
    private String msg;
    private T data;

    private ApiResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponse<T> succeed(T data) {
        return new ApiResponse<>(null, SUCCESS, data);
    }

    public static <Void> ApiResponse<Void> fail(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
}
