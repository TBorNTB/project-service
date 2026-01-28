package com.sejong.projectservice.domains.qna.repository;

import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import com.sejong.projectservice.domains.qna.repository.projection.QuestionAnswerCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswerEntity, Long> {

    @EntityGraph(attributePaths = {"questionEntity"})
    Page<QuestionAnswerEntity> findAllByQuestionEntityId(Long questionId, Pageable pageable);

    @EntityGraph(attributePaths = {"questionEntity"})
    List<QuestionAnswerEntity> findAllByQuestionEntityId(Long questionId);

    boolean existsByQuestionEntityIdAndAcceptedTrue(Long questionId);

    boolean existsByQuestionEntityIdAndAcceptedTrueAndIdNot(Long questionId, Long id);

    @Query("select qa.questionEntity.id as questionId, count(qa) as cnt " +
        "from QuestionAnswerEntity qa " +
        "where qa.questionEntity.id in :questionIds " +
        "group by qa.questionEntity.id")
    List<QuestionAnswerCountProjection> countByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
