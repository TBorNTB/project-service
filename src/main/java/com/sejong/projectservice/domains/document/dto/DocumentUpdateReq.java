package com.sejong.projectservice.domains.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentUpdateReq {
    private String title;
    private String description;
    private String content;

    @Schema(description = "썸네일 이미지 key (presigned URL 업로드 후 받은 key)")
    private String thumbnailKey;

    @Schema(description = "에디터 본문에 삽입된 이미지 key 목록")
    private List<String> contentImageKeys;
}
