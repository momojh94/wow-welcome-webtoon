package com.webtoon.api.common.aop;

import com.webtoon.core.common.ApiResponse;
import com.webtoon.core.common.exception.ApplicationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.webtoon.core.common.exception.ExceptionType.INVALID_REQUEST_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApplicationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleApplicationException(ApplicationException exception) {
        return ResponseEntity.status(exception.getErrorType()
                                              .getStatus())
                             .body(ApiResponse.fail(exception.getErrorType()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                                  .getAllErrors().stream()
                                  .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                  .collect(Collectors.joining(System.lineSeparator()));
        ApiResponse<String> response = ApiResponse.fail(INVALID_REQUEST_VALUE, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }
}
