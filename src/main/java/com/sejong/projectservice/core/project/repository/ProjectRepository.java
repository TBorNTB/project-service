package com.sejong.projectservice.core.project.repository;

import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository {

    Project save(Project project);

    Page<Project> findAll(Pageable pageable);

    Project update(Project project, Long projectId);

    Page<Project> searchWithFilters(String keyword, Category category, ProjectStatus status, Pageable pageable);

    Project findOne(Long projectId);

    boolean existsById(Long postId);
}
