package com.webtoon.api.common.aop;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.common.exception.BadRequestException;
import com.webtoon.core.common.exception.ConflictException;
import com.webtoon.core.common.exception.NotFoundException;
import com.webtoon.core.common.exception.UnauthorizedException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.webtoon.core.common.exception.ExceptionType.INTERNAL_SERVER;
import static com.webtoon.core.common.exception.ExceptionType.INVALID_REQUEST_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                                  .getAllErrors().stream()
                                  .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                  .collect(Collectors.joining(System.lineSeparator()));
        return ApiResponse.fail(INVALID_REQUEST_VALUE.getErrorCode(), message);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequestException(BadRequestException ex) {
        return ApiResponse.fail(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleConflictException(ConflictException ex) {
        return ApiResponse.fail(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFoundException(NotFoundException ex) {
        return ApiResponse.fail(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnauthorizedException(UnauthorizedException ex) {
        return ApiResponse.fail(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        return ApiResponse.fail(INTERNAL_SERVER.getErrorCode(), INTERNAL_SERVER.getMessage());
    }
}
