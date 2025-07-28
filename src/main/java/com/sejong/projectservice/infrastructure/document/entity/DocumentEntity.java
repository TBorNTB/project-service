package com.sejong.projectservice.infrastructure.document.entity;

import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
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
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    public static DocumentEntity from(Document document) {
        return DocumentEntity.builder()
                .id(document.getId() != null ? document.getId() : null)
                .yorkieDocumentId(document.getYorkieDocumentId())
                .title(document.getTitle())
                .description(document.getDescription())
                .thumbnailUrl(document.getThumbnailUrl())
                .content(document.getContent())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .projectEntity(null)
                .build();
    }

    public void update(Document document) {
        this.title = document.getTitle();
        this.description = document.getDescription();
        this.thumbnailUrl = document.getThumbnailUrl();
        this.content = document.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public Document toDomain() {
        return Document.builder()
                .id(id)
                .yorkieDocumentId(yorkieDocumentId)
                .title(title)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .content(content)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .projectId(projectEntity != null ? projectEntity.getId() : null)
                .build();
    }

    public void assignDocumentEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }
}
