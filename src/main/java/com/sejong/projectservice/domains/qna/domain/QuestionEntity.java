package com.sejong.projectservice.domains.qna.domain;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.qna.enums.QuestionStatus;
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
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "question")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private String username;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private QuestionStatus questionStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "questionEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuestionAnswerEntity> questionAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "questionEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuestionCategoryEntity> questionCategories = new ArrayList<>();

    public static QuestionEntity of(
            String title,
            String description,
            String content,
            String username,
            List<CategoryEntity> categories
    ) {
        LocalDateTime now = LocalDateTime.now();
        QuestionEntity question = QuestionEntity.builder()
                .id(null)
                .title(title)
                .description(description)
                .content(content)
                .username(username)
                .questionStatus(QuestionStatus.NOT_ACCEPTED)
                .createdAt(now)
                .updatedAt(now)
                .questionAnswers(new ArrayList<>())
                .questionCategories(new ArrayList<>())
                .build();

        if (categories != null) {
            categories.forEach(question::addCategory);
        }
        return question;
    }

    public void addCategory(CategoryEntity categoryEntity) {
        if (categoryEntity == null) {
            return;
        }
        QuestionCategoryEntity link = QuestionCategoryEntity.of(this, categoryEntity);
        this.questionCategories.add(link);
    }

    public void validateOwnerPermission(String username) {
        if (username == null || !username.equals(this.username)) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }

    public void update(String title, String description, String content, List<CategoryEntity> categories) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.updatedAt = LocalDateTime.now();

        if (categories != null) {
            replaceCategories(categories);
        }
    }

    private void replaceCategories(List<CategoryEntity> categories) {
        if (categories == null) {
            return;
        }

        Set<Long> desiredCategoryIds = new LinkedHashSet<>();
        for (CategoryEntity category : categories) {
            if (category != null && category.getId() != null) {
                desiredCategoryIds.add(category.getId());
            }
        }

        this.questionCategories.removeIf(link -> {
            if (link == null || link.getCategoryEntity() == null) {
                return true;
            }
            Long categoryId = link.getCategoryEntity().getId();
            return categoryId == null || !desiredCategoryIds.contains(categoryId);
        });

        Set<Long> existingCategoryIds = new LinkedHashSet<>();
        for (QuestionCategoryEntity link : this.questionCategories) {
            if (link != null && link.getCategoryEntity() != null && link.getCategoryEntity().getId() != null) {
                existingCategoryIds.add(link.getCategoryEntity().getId());
            }
        }

        for (CategoryEntity category : categories) {
            if (category == null) {
                continue;
            }
            Long categoryId = category.getId();
            if (categoryId == null) {
                continue;
            }
            if (existingCategoryIds.add(categoryId)) {
                addCategory(category);
            }
        }
    }

    public void assignQuestionAnswer(QuestionAnswerEntity questionAnswerEntity) {
        this.questionAnswers.add(questionAnswerEntity);
    }

    public void markAccepted() {
        this.questionStatus = QuestionStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markNotAccepted() {
        this.questionStatus = QuestionStatus.NOT_ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAcceptedToggle() {
        if(QuestionStatus.ACCEPTED.equals(questionStatus)){
            this.questionStatus = QuestionStatus.NOT_ACCEPTED;
        }
        else{
            this.questionStatus = QuestionStatus.ACCEPTED;
        }

        this.updatedAt = LocalDateTime.now();
    }
}
