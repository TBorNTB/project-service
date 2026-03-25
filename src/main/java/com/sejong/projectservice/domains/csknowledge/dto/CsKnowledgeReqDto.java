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

        @NotBlank(message = "요약은 필수입니다")
        String description,

        @NotNull(message = "카테고리는 필수입니다")
        String category,

        @Schema(description = "썸네일 이미지 key (presigned URL 업로드 후 받은 key)")
        String thumbnailKey,

        @Schema(description = "에디터 본문에 삽입된 이미지 key 목록")
        List<String> contentImageKeys,

        @Schema(description = "새로 첨부할 파일 목록 (presigned URL 업로드 후 받은 tempKey + 원본 파일명)")
        List<AttachmentReq> attachments,

        @Schema(description = "삭제할 첨부 파일 key 목록 (수정 시에만 사용)")
        List<String> attachmentKeysToDelete
) {
    public record AttachmentReq(
            @Schema(description = "presigned URL 업로드 후 받은 temp key")
            String tempKey,
            @Schema(description = "원본 파일명 (예: report.pdf)")
            String originalFileName
    ) {}
}