package com.sejong.projectservice.support.common.sanitizer;

import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;

import java.util.List;

/**
 * XSS 방어를 위한 요청 Sanitizer 추상화.
 * 정책 변경 시 구현체만 수정하면 되도록 인터페이스로 분리.
 */
public interface RequestSanitizer {

    /** Project 생성 요청 필드 Sanitize (원본 수정) */
    void sanitize(ProjectFormRequest request);

    /** Project 수정 요청 필드 Sanitize (원본 수정) */
    void sanitize(ProjectUpdateRequest request);

    /** CS Knowledge 요청 Sanitize (record 불변 → Sanitize 결과 반환) */
    SanitizedCsKnowledge sanitize(CsKnowledgeReqDto request);

    /** News 요청 Sanitize (원본 수정) */
    void sanitize(NewsReqDto request);

    /** QnA 질문 입력 Sanitize (title, description, content, categories) */
    SanitizedQuestionInput sanitizeQuestionInput(String title, String description, String content, List<String> categories);

    /** QnA 답변 본문 Sanitize */
    String sanitizeAnswerContent(String content);
}
