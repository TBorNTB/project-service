package com.sejong.projectservice.domains.csknowledge.dto;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;

import java.time.LocalDateTime;

public record CsKnowledgeResDto(
        Long id,
        String title,
        String content,
        String writerId,
        String nickname,
        String category,
        LocalDateTime createdAt
) {

    public static CsKnowledgeResDto from(CsKnowledgeEntity csKnowledgeEntity, String nickname) {
        return new CsKnowledgeResDto(
                csKnowledgeEntity.getId(),
                csKnowledgeEntity.getTitle(),
                csKnowledgeEntity.getContent(),
                csKnowledgeEntity.getWriterId(),
                nickname,
                csKnowledgeEntity.getCategoryEntity().getName(),
                csKnowledgeEntity.getCreatedAt()
        );
    }
}