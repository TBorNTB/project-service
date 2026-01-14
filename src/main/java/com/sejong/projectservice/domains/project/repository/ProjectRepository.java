package com.sejong.projectservice.domains.project.repository;

import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("SELECT p FROM ProjectEntity p " +
            "WHERE (:keyword IS NULL OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "AND (:status IS NULL OR p.projectStatus = :status)")
    Page<ProjectEntity> searchWithFilters(@Param("keyword") String keyword,
                                          @Param("status") ProjectStatus status,
                                          Pageable pageable);


    @Query("select count(pe) from ProjectEntity pe")
    Long getProjectCount();

    @Query("SELECT count(pe) FROM ProjectEntity pe " +
            "WHERE pe.createdAt >= :startDate AND pe.createdAt < :endDate")
    Long getProjectCountByDate(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT pe.id FROM ProjectEntity pe WHERE pe.username = :username")
    List<Long> findProjectIdsByUsername(@Param("username") String username);
}
