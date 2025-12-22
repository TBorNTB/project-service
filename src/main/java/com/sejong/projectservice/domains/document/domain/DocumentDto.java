package com.sejong.projectservice.domains.document.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    private Long id;
    private String yorkieDocumentId;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long projectId;

    public static List<Document> from2(List<DocumentEntity> documents) {
        return documents.stream()
                .map(it->{
                    return Document.builder()
                            .id(it.getId())
                            .yorkieDocumentId(it.getYorkieDocumentId())
                            .title(it.getTitle())
                            .content(it.getContent())
                            .description(it.getDescription())
                            .thumbnailUrl(it.getThumbnailUrl())
                            .createdAt(it.getCreatedAt())
                            .updatedAt(it.getUpdatedAt())
                            .build();
                }).toList();

    }

    public void update(String title, String content, String description, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }
}
