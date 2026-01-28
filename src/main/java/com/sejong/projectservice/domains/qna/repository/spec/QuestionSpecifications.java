package com.sejong.projectservice.domains.qna.repository.spec;

import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import com.sejong.projectservice.domains.qna.domain.QuestionCategoryEntity;
import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
import com.sejong.projectservice.domains.qna.enums.QuestionListStatusFilter;
import com.sejong.projectservice.domains.qna.enums.QuestionStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Subquery;

import java.util.List;

public final class QuestionSpecifications {

    private QuestionSpecifications() {
    }

    public static Specification<QuestionEntity> filter(
        QuestionListStatusFilter statusFilter,
        List<String> categoryNames,
        String keyword
    ) {
        Specification<QuestionEntity> spec = (root, query, cb) -> cb.conjunction();

        Specification<QuestionEntity> statusSpec = byStatus(statusFilter);
        if (statusSpec != null) {
            spec = spec.and(statusSpec);
        }

        Specification<QuestionEntity> categorySpec = byCategoryNames(categoryNames);
        if (categorySpec != null) {
            spec = spec.and(categorySpec);
        }

        Specification<QuestionEntity> keywordSpec = byKeyword(keyword);
        if (keywordSpec != null) {
            spec = spec.and(keywordSpec);
        }

        return spec;
    }

    private static Specification<QuestionEntity> byStatus(QuestionListStatusFilter statusFilter) {
        if (statusFilter == null || statusFilter == QuestionListStatusFilter.ALL) {
            return null;
        }

        return (root, query, cb) -> {
            if (statusFilter == QuestionListStatusFilter.ACCEPTED) {
                return cb.equal(root.get("questionStatus"), QuestionStatus.ACCEPTED);
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            var answerRoot = subquery.from(QuestionAnswerEntity.class);
            subquery.select(cb.literal(1L));
            subquery.where(cb.equal(answerRoot.get("questionEntity").get("id"), root.get("id")));

            if (statusFilter == QuestionListStatusFilter.ANSWERED) {
                return cb.exists(subquery);
            }
            if (statusFilter == QuestionListStatusFilter.UNANSWERED) {
                return cb.not(cb.exists(subquery));
            }
            return cb.conjunction();
        };
    }

    private static Specification<QuestionEntity> byCategoryNames(List<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            query.distinct(true);
            Join<QuestionEntity, QuestionCategoryEntity> join = root.join("questionCategories", JoinType.INNER);
            List<String> lowered = categoryNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toLowerCase())
                .toList();

            if (lowered.isEmpty()) {
                return cb.conjunction();
            }

            return cb.lower(join.get("categoryEntity").get("name")).in(lowered);
        };
    }

    private static Specification<QuestionEntity> byKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String like = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
            cb.like(cb.lower(root.get("title")), like),
            cb.like(cb.lower(root.get("description")), like),
            cb.like(cb.lower(root.get("content")), like)
        );
    }
}
