package com.sejong.projectservice.core.document.domain;

import java.time.LocalDateTime;
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
}
