package com.sejong.archiveservice.application.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionType implements ExceptionTypeIfs {

    // 성공
    OK(200, "성공"),
    
    // 400 Bad Request
    BAD_REQUEST(400, "잘못된 요청"),
    INVALID_INPUT(400, "입력값이 유효하지 않습니다"),
    MISSING_REQUIRED_FIELD(400, "필수 입력값이 누락되었습니다"),
    
    // 401 Unauthorized
    UNAUTHORIZED(401, "인증이 필요합니다"),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다"),
    
    // 403 Forbidden
    FORBIDDEN(403, "접근 권한이 없습니다"),
    NOT_NEWS_OWNER(403, "뉴스 작성자만 수정/삭제할 수 있습니다"),
    
    // 404 Not Found
    NOT_FOUND(404, "리소스를 찾을 수 없습니다"),
    NEWS_NOT_FOUND(404, "뉴스를 찾을 수 없습니다"),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    
    // 500 Internal Server Error
    SERVER_ERROR(500, "서버 내부 오류"),
    DATABASE_ERROR(500, "데이터베이스 오류"),
    EXTERNAL_SERVICE_ERROR(503, "외부 서비스 연결 실패"),
    KAFKA_PUBLISH_ERROR(500, "이벤트 발행 실패");

    private final Integer httpStatus;
    private final String description;

    @Override
    public Integer httpStatus() {
        return httpStatus;
    }

    @Override
    public String description() {
        return description;
    }
}