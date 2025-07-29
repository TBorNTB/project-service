package com.sejong.projectservice.application.common.error.code;

public interface ErrorCodeIfs {
    Integer getHttpStatusCode();

    Integer getErrorCode();

    String getDescription();
}
