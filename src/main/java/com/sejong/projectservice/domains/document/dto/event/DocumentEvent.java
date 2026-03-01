package com.sejong.projectservice.domains.document.dto.event;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.support.common.file.FileUploader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentEvent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String id;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private String createdAt;
    private String updatedAt;

    public static DocumentEvent from(DocumentEntity documentEntity, FileUploader fileUploader) {
        String thumbnailUrl = documentEntity.getThumbnailKey() != null && !documentEntity.getThumbnailKey().isEmpty()
                ? fileUploader.getFileUrl(documentEntity.getThumbnailKey())
                : null;
        return DocumentEvent.builder()
                .id(documentEntity.getId().toString())
                .title(documentEntity.getTitle())
                .description(documentEntity.getDescription())
                .thumbnailUrl(thumbnailUrl)
                .content(documentEntity.getContent())
                .createdAt(documentEntity.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(documentEntity.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}
