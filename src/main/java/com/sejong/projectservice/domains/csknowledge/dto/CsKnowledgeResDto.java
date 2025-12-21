package com.sejong.projectservice.domains.csknowledge.dto;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledge;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;

import java.time.LocalDateTime;

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