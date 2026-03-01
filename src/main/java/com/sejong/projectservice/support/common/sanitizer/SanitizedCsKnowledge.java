package com.sejong.projectservice.support.common.sanitizer;

/**
 * CS Knowledge 요청 Sanitize 결과 (record 불변이라 별도 보관).
 */
public record SanitizedCsKnowledge(
        String title,
        String content,
        String description,
        String category
) {}
