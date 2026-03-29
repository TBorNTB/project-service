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

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "cs_knowledge_reference_link", joinColumns = @JoinColumn(name = "cs_knowledge_id"))
    @OrderColumn(name = "sort_order")
    @BatchSize(size = 30)
    private List<CsKnowledgeReferenceLink> referenceLinks = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "cs_knowledge_attachment", joinColumns = @JoinColumn(name = "cs_knowledge_id"))
    @BatchSize(size = 30)
    private List<CsKnowledgeAttachment> attachments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CsKnowledgeEntity of(String title, String content, String description, List<String> referenceLinkUrls,
                                         String username, CategoryEntity categoryEntity, LocalDateTime time) {
        return CsKnowledgeEntity.builder()
                .id(null)
                .title(title)
                .writerId(username)
                .content(content)
                .description(description)
                .referenceLinks(toReferenceLinkEmbeddables(referenceLinkUrls))
                .categoryEntity(categoryEntity)
                .createdAt(time)
                .updatedAt(time)
                .build();
    }

    public void update(String title, String content, String description, List<String> referenceLinkUrls, String username,
                       CategoryEntity categoryEntity, LocalDateTime updatedAt) {
        this.title = title;
        this.writerId = username;
        this.content = content;
        this.description = description;
        this.referenceLinks.clear();
        this.referenceLinks.addAll(toReferenceLinkEmbeddables(referenceLinkUrls));
        this.categoryEntity = categoryEntity;
        this.updatedAt = updatedAt;
    }

    private static List<CsKnowledgeReferenceLink> toReferenceLinkEmbeddables(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return new ArrayList<>();
        }
        List<CsKnowledgeReferenceLink> out = new ArrayList<>();
        for (String u : urls) {
            if (u != null && !u.isBlank()) {
                out.add(new CsKnowledgeReferenceLink(u.trim()));
            }
        }
        return out;
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
