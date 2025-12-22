package com.sejong.projectservice.domains.document.kafka;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
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
    private String yorkieDocumentId;

    private String title;
    private String content;
    private String description;
    private String thumbnailUrl;

    private String createdAt;
    private String updatedAt;

    public static DocumentEvent from(DocumentEntity document){
        return DocumentEvent.builder()
                .id(document.getId().toString())
                .yorkieDocumentId(document.getYorkieDocumentId())
                .title(document.getTitle())
                .description(document.getDescription())
                .thumbnailUrl(document.getThumbnailUrl())
                .content(document.getContent())
                .createdAt(document.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(document.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .build();
    }
}
