package com.sejong.projectservice.infrastructure.csknowledge;


import com.sejong.projectservice.core.csknowledge.CsKnowledge;
import com.sejong.projectservice.core.csknowledge.TechCategory;
import com.sejong.projectservice.core.user.UserId;
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
                @Index(name = "idx_cs_knowledge_category_id", columnList = "categoryName, id")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "categoryName", columnDefinition = "VARCHAR(50)", nullable = false)
    private TechCategory techCategory;

    private LocalDateTime createdAt;

    public CsKnowledge toDomain() {
        return CsKnowledge.builder()
                .id(id)
                .title(title)
                .writerId(UserId.of(writerId))
                .content(content)
                .category(techCategory)
                .createdAt(createdAt)
                .build();
    }

    public static CsKnowledgeEntity from(CsKnowledge knowledge) {
        return CsKnowledgeEntity.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .writerId(knowledge.getWriterId().userId())
                .content(knowledge.getContent())
                .techCategory(knowledge.getCategory())
                .createdAt(knowledge.getCreatedAt())
                .build();
    }
}
