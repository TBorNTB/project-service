package com.sejong.projectservice.support.common.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionType implements ExceptionTypeIfs {

    // 400 Bad Request
    BAD_REQUEST(400, "잘못된 요청"),
    INVALID_INPUT(400, "입력값이 유효하지 않습니다"),
    MISSING_REQUIRED_FIELD(400, "필수 입력값이 누락되었습니다"),
    REQUIRED_ADMIN(400, "어드민만 가능한 요청입니다."),
    SORT_DIRECTION(400, "정렬 방향을 확인 해주세요"),

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
    CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다"),
    PROJECT_NOT_FOUND(404, "프로젝트를 찾을 수 없습니다"),
    CS_KNOWLEDGE_NOT_FOUND(404, "CS게시물을 찾을 수 없습니다"),
    DOCUMENT_NOT_FOUND(404, "document를 찾을 수 없습니다"),
    SUBGOAL_NOT_FOUND(404, "하위목표를 찾을 수 없습니다"),
    TECHSTACK_NOT_FOUND(404, "기술스택을 찾을 수 없습니다"),
    QUESTION_NOT_FOUND(404, "질문글을 찾을 수 없습니다"),
    QUESTION_ANSWER_NOT_FOUND(404, "질문 답변을 찾을 수 없습니다"),

    // 500 Internal Server Error
    SERVER_ERROR(500, "서버 내부 오류"),
    DATABASE_ERROR(500, "데이터베이스 오류"),
    EXTERNAL_SERVICE_ERROR(503, "외부 서비스 연결 실패"),
    KAFKA_PUBLISH_ERROR(500, "이벤트 발행 실패"),
    JSON_PARSHING_ERROR(500, "Json 파싱 에러"),
    FILE_REMOVE_FAIL(500, "S3 파일 삭제 실패"),
    ;

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