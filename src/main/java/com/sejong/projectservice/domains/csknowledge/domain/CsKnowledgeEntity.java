package com.sejong.projectservice.domains.csknowledge.domain;


import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "cs_knowledge",
        indexes = {
                @Index(name = "idx_cs_knowledge_category_id", columnList = "category_id, id")
        }
)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "writer_id", nullable = false)
    private String writerId;

    @Lob
    private String content;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "thumbnail_key")
    private String thumbnailKey;

    @ElementCollection
    @CollectionTable(name = "cs_knowledge_attachment", joinColumns = @JoinColumn(name = "cs_knowledge_id"))
    @BatchSize(size = 30)
    private List<CsKnowledgeAttachment> attachments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CsKnowledgeEntity of(String title, String content, String description, String username, CategoryEntity categoryEntity, LocalDateTime time) {
        return CsKnowledgeEntity.builder()
                .id(null)
                .title(title)
                .writerId(username)
                .content(content)
                .description(description)
                .categoryEntity(categoryEntity)
                .createdAt(time)
                .updatedAt(time)
                .build();
    }

    public void update(String title, String content, String description ,String username, CategoryEntity categoryEntity, LocalDateTime updatedAt) {
        this.title = title;
        this.writerId = username;
        this.content = content;
        this.description = description;
        this.categoryEntity = categoryEntity;
        this.updatedAt = updatedAt;
    }

    public void validateOwnerPermission(String username) {
        if (!writerId.equals(username)) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }

    public void validateOwnerPermission(String username, String userRole) {
        if (!writerId.equals(username) && !userRole.equalsIgnoreCase("ADMIN")) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }

    public void updateThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }

    public void addAttachment(CsKnowledgeAttachment attachment) {
        this.attachments.add(attachment);
    }

    public void removeAttachmentByKey(String fileKey) {
        this.attachments.removeIf(a -> a.getFileKey().equals(fileKey));
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }
}
