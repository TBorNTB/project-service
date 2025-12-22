package com.sejong.projectservice.domains.csknowledge.dto;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeDto;
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
    public static CsKnowledgeResDto from(CsKnowledgeDto csKnowledgeDto, String nickname) {
        return new CsKnowledgeResDto(
                csKnowledgeDto.getId(),
                csKnowledgeDto.getTitle(),
                csKnowledgeDto.getContent(),
                csKnowledgeDto.getWriterId().userId(),
                nickname,
                csKnowledgeDto.getCategory(),
                csKnowledgeDto.getCreatedAt()
        );
    }
}