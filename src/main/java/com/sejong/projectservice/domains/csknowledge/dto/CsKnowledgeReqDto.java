package com.sejong.projectservice.domains.csknowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CsKnowledgeReqDto(
        @NotBlank(message = "제목은 필수입니다")
        String title,

        @NotBlank(message = "내용은 필수입니다")
        String content,

        @NotNull(message = "카테고리는 필수입니다")
        String category,

        @Schema(description = "에디터 본문에 삽입된 이미지 key 목록")
        List<String> contentImageKeys
) {
}