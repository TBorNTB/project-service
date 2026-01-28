package com.sejong.projectservice.domains.qna.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "question_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_question_category", columnNames = {"question_id", "category_id"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class QuestionCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity questionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    public static QuestionCategoryEntity of(QuestionEntity questionEntity, CategoryEntity categoryEntity) {
        return QuestionCategoryEntity.builder()
                .questionEntity(questionEntity)
                .categoryEntity(categoryEntity)
                .build();
    }
}
