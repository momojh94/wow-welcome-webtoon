package com.www.core.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            BadRequestException.class,
    })
    public ApiResponse<Void> handleBadRequestException(BadRequestException e) {
        ApiResponse<Void> apiResponse = ApiResponse.fail(e.getErrorCode(), e.getMessage());
        return apiResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {
            ResourceNotFoundException.class,
    })
    public ApiResponse<Void> handleNotFoundException(ResourceNotFoundException e) {
        ApiResponse<Void> apiResponse = ApiResponse.fail(e.getErrorCode(), e.getMessage());
        return apiResponse;
    }
}
