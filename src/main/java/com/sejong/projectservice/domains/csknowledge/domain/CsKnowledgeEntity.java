package com.sejong.projectservice.domains.csknowledge.domain;


import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CsKnowledgeEntity of(String title, String content, String username, CategoryEntity categoryEntity, LocalDateTime time) {
        return CsKnowledgeEntity.builder()
                .id(null)
                .title(title)
                .writerId(username)
                .content(content)
                .categoryEntity(categoryEntity)
                .createdAt(time)
                .updatedAt(time)
                .build();
    }

    public void update(String title, String content, String username, CategoryEntity categoryEntity, LocalDateTime updatedAt) {
        this.title = title;
        this.writerId = username;
        this.content = content;
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
}
