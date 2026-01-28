package com.sejong.projectservice.domains.qna.repository;

import com.sejong.projectservice.domains.qna.domain.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity,Long> {

	@EntityGraph(attributePaths = {"questionCategories", "questionCategories.categoryEntity"})
	Optional<QuestionEntity> findWithCategoriesById(Long id);

	@Override
	@EntityGraph(attributePaths = {"questionCategories", "questionCategories.categoryEntity"})
	Page<QuestionEntity> findAll(Pageable pageable);
}
