package com.sejong.projectservice.application.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeIfs {

    OK(200, 0, "성공"),
    BAD_REQUEST(400, 0, "잘못된 요청"),
    SERVER_ERROR(500, 0, "서버 에러"),

    NULL_POINT(500, 0, "Null Pointer"),
    MULTI_REQUEST(405, 0, "하루 한번만 요청 가능합니다"),
    EXTERNAL_SERVER_ERROR(500, 0, "서킷 브레이커가 작동했습니다.");

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}
