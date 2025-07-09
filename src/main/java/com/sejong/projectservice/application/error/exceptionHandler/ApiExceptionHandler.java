package com.sejong.projectservice.application.error.exceptionHandler;

import com.sejong.projectservice.application.error.api.ErrorResponse;
import com.sejong.projectservice.application.error.code.ErrorCodeIfs;
import com.sejong.projectservice.application.error.exception.ApiException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
@Order(value=Integer.MIN_VALUE)
public class ApiExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Object> apiException(
            ApiException apiException
    ){
        ErrorCodeIfs errorCode = apiException.getErrorCodeIfs();
        ErrorResponse errorResponse = ErrorResponse.ERROR(errorCode, apiException.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(errorResponse);
    }
}
