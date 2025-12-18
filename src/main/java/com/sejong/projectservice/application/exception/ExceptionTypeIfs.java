package com.sejong.archiveservice.application.exception;

public interface ExceptionTypeIfs {
    Integer httpStatus();
    String description();
}