package com.sejong.archiveservice.application.exception;

import lombok.Data;

@Data
public class ExceptionResponse {
    private final String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }
}