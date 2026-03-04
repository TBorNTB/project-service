package com.sejong.projectservice.domains.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryUpdateReq {
    @NotBlank
    private String name;

    private String description;

    private String content;

    @Schema(description = "아이콘 이미지 key (presigned URL 업로드 후 받은 key)")
    private String iconKey;
}
