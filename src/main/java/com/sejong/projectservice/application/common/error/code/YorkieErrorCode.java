package com.sejong.projectservice.application.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum YorkieErrorCode implements ErrorCodeIfs {

    NOT_FOUND_YORKIE_ID(HttpStatus.NOT_FOUND.value(), 4041, "존재하지 않는 yorkieId입니다"),

    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}
