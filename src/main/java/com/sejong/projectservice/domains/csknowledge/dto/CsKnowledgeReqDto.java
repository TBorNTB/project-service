package com.sejong.projectservice.domains.csknowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CsKnowledgeReqDto(
        @NotBlank(message = "제목은 필수입니다")
        String title,

        @NotBlank(message = "내용은 필수입니다")
        String content,

        @NotNull(message = "카테고리는 필수입니다")
        String category
) {
}