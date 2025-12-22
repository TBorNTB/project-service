package com.sejong.projectservice.domains.document.dto;

import java.time.LocalDateTime;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
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
    private String yorkieDocumentId;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DocumentInfoRes from(DocumentEntity documentEntity) {
        return DocumentInfoRes.builder()
                .id(documentEntity.getId())
                .yorkieDocumentId(documentEntity.getYorkieDocumentId())
                .title(documentEntity.getTitle())
                .content(documentEntity.getContent())
                .description(documentEntity.getDescription())
                .thumbnailUrl(documentEntity.getThumbnailUrl())
                .createdAt(documentEntity.getCreatedAt())
                .updatedAt(documentEntity.getUpdatedAt())
                .build();
    }
}

