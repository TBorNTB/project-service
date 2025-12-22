package com.sejong.projectservice.domains.csknowledge.domain;


import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.enums.TechCategory;
import com.sejong.projectservice.domains.user.UserId;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CsKnowledgeDto toDto() {
        return CsKnowledgeDto.builder()
                .id(id)
                .title(title)
                .writerId(UserId.of(writerId))
                .content(content)
                .category(categoryEntity.getName())
                .createdAt(createdAt)
                .build();
    }

    public static CsKnowledgeEntity from(CsKnowledgeDto knowledge, CategoryEntity categoryEntity) {
        return CsKnowledgeEntity.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .writerId(knowledge.getWriterId().userId())
                .content(knowledge.getContent())
                .categoryEntity(categoryEntity)
                .createdAt(knowledge.getCreatedAt())
                .updatedAt(knowledge.getCreatedAt())
                .build();
    }

    public void update(CsKnowledgeReqDto reqDto, LocalDateTime updatedAt, String username, CategoryEntity categoryEntity) {
        this.title = reqDto.title();
        this.writerId = username;
        this.content = reqDto.content();
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
