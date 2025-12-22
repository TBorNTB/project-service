package com.sejong.projectservice.domains.project.repository;

import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("SELECT p FROM ProjectEntity p " +
            "WHERE (:keyword IS NULL OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "AND (:status IS NULL OR p.projectStatus = :status)")
    Page<ProjectEntity> searchWithFilters(@Param("keyword") String keyword,
                                          @Param("status") ProjectStatus status,
                                          Pageable pageable);


    @Query("select count(pe) from ProjectEntity pe")
    Long getProjectCount();
}
