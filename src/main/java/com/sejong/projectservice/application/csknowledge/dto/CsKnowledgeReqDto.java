package com.sejong.archiveservice.application.csknowledge.dto;

import com.sejong.archiveservice.core.csknowledge.TechCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CsKnowledgeReqDto(
        @NotBlank(message = "제목은 필수입니다")
        String title,
        
        @NotBlank(message = "내용은 필수입니다")
        String content,
        
        @NotNull(message = "카테고리는 필수입니다")
        TechCategory category
) {
}