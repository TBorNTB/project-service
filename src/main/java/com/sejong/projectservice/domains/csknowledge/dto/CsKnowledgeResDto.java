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

    public static CsKnowledgeResDto from(CsKnowledgeEntity csKnowledgeDto, String nickname) {
        return new CsKnowledgeResDto(
                csKnowledgeDto.getId(),
                csKnowledgeDto.getTitle(),
                csKnowledgeDto.getContent(),
                csKnowledgeDto.getWriterId(),
                nickname,
                csKnowledgeDto.getCategoryEntity().getName(),
                csKnowledgeDto.getCreatedAt()
        );
    }
}