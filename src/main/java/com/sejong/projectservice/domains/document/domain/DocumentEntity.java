package com.sejong.projectservice.domains.document.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DocumentEntity {

    @Id
    @Column(name = "document_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "yorkie_document_id", nullable = false, unique = true)
    private String yorkieDocumentId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    public static DocumentEntity from(DocumentDto documentDto) {
        return DocumentEntity.builder()
                .id(documentDto.getId() != null ? documentDto.getId() : null)
                .yorkieDocumentId(documentDto.getYorkieDocumentId())
                .title(documentDto.getTitle())
                .description(documentDto.getDescription())
                .thumbnailUrl(documentDto.getThumbnailUrl())
                .content(documentDto.getContent())
                .createdAt(documentDto.getCreatedAt())
                .updatedAt(documentDto.getUpdatedAt())
                .projectEntity(null)
                .build();
    }

    public static DocumentEntity from(DocumentDto documentDto, ProjectEntity projectEntity) {
        return DocumentEntity.builder()
                .id(documentDto.getId() != null ? documentDto.getId() : null)
                .yorkieDocumentId(documentDto.getYorkieDocumentId())
                .title(documentDto.getTitle())
                .description(documentDto.getDescription())
                .thumbnailUrl(documentDto.getThumbnailUrl())
                .content(documentDto.getContent())
                .createdAt(documentDto.getCreatedAt())
                .updatedAt(documentDto.getUpdatedAt())
                .projectEntity(projectEntity)
                .build();
    }

    public static DocumentEntity of(DocumentCreateReq request, String yorkieDocumentId, ProjectEntity projectEntity) {

        DocumentEntity documentEntity = DocumentEntity.builder()
                .id(null)
                .yorkieDocumentId(yorkieDocumentId)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectEntity(projectEntity)
                .build();
        projectEntity.addDocument(documentEntity);

        return documentEntity;
    }

    public void update(DocumentDto documentDto) {
        this.title = documentDto.getTitle();
        this.description = documentDto.getDescription();
        this.thumbnailUrl = documentDto.getThumbnailUrl();
        this.content = documentDto.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public DocumentDto toDomain() {
        return DocumentDto.builder()
                .id(id)
                .yorkieDocumentId(yorkieDocumentId)
                .title(title)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .content(content)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void assignDocumentEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public void update(String title, String content, String description, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
