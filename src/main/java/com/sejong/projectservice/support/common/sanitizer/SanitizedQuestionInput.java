package com.sejong.projectservice.support.common.sanitizer;

import java.util.List;

/**
 * QnA 질문 입력 Sanitize 결과.
 */
public record SanitizedQuestionInput(
        String title,
        String description,
        String content,
        List<String> categories
) {}
