package com.sejong.projectservice.domains.document.dto;

import java.time.LocalDateTime;

import com.sejong.projectservice.domains.document.domain.Document;
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

    public static DocumentInfoRes from(Document document) {
        return DocumentInfoRes.builder()
                .id(document.getId())
                .yorkieDocumentId(document.getYorkieDocumentId())
                .title(document.getTitle())
                .content(document.getContent())
                .description(document.getDescription())
                .thumbnailUrl(document.getThumbnailUrl())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

