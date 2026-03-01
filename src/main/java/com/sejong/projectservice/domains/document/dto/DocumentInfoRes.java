package com.sejong.projectservice.domains.document.dto;

import java.time.LocalDateTime;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentInfoRes {
    private Long id;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DocumentInfoRes from(DocumentEntity documentEntity, FileUploader fileUploader) {
        String thumbnailUrl = documentEntity.getThumbnailKey() != null && !documentEntity.getThumbnailKey().isEmpty()
                ? fileUploader.getFileUrl(documentEntity.getThumbnailKey())
                : null;
        return DocumentInfoRes.builder()
                .id(documentEntity.getId())
                .title(documentEntity.getTitle())
                .content(documentEntity.getContent())
                .description(documentEntity.getDescription())
                .thumbnailUrl(thumbnailUrl)
                .createdAt(documentEntity.getCreatedAt())
                .updatedAt(documentEntity.getUpdatedAt())
                .build();
    }
}

