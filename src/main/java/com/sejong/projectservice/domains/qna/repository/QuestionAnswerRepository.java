package com.sejong.projectservice.domains.qna.repository;

import com.sejong.projectservice.domains.qna.domain.QuestionAnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswerEntity, Long> {

    @EntityGraph(attributePaths = {"questionEntity"})
    Page<QuestionAnswerEntity> findAllByQuestionEntityId(Long questionId, Pageable pageable);

    @EntityGraph(attributePaths = {"questionEntity"})
    List<QuestionAnswerEntity> findAllByQuestionEntityId(Long questionId);

    boolean existsByQuestionEntityIdAndAcceptedTrue(Long questionId);

    boolean existsByQuestionEntityIdAndAcceptedTrueAndIdNot(Long questionId, Long id);
}
