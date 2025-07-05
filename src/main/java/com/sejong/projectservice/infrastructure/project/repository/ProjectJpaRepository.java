package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("SELECT p FROM ProjectEntity p "+
    "WHERE (:keyword IS NULL OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) "+
            "AND (:category IS NULL OR p.category = :category) "+
            "AND (:status IS NULL OR p.projectStatus = :status)")
    Page<ProjectEntity> searchWithFilters(@Param("keyword")String keyword,
                                          @Param("category")Category category,
                                          @Param("status")ProjectStatus status,
                                          Pageable pageable);
}
