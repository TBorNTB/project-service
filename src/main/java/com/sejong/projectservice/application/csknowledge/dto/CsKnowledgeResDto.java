package com.sejong.archiveservice.application.csknowledge.dto;

import com.sejong.archiveservice.core.csknowledge.CsKnowledge;
import com.sejong.archiveservice.core.csknowledge.TechCategory;
import java.time.LocalDateTime;
import java.util.Map;

public record CsKnowledgeResDto(
        Long id,
        String title,
        String content,
        String writerId,
        String nickname,
        TechCategory category,
        LocalDateTime createdAt
) {
    public static CsKnowledgeResDto from(CsKnowledge csKnowledge, String nickname) {
        return new CsKnowledgeResDto(
                csKnowledge.getId(),
                csKnowledge.getTitle(),
                csKnowledge.getContent(),
                csKnowledge.getWriterId().userId(),
                nickname,
                csKnowledge.getCategory(),
                csKnowledge.getCreatedAt()
        );
    }
}