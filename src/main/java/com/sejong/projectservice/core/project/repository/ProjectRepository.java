package com.sejong.projectservice.core.project.repository;

import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository {

    Project save(Project project);

    Page<Project> findAll(Pageable pageable);

    Page<Project> searchWithFilters(String keyword, ProjectStatus status, Pageable pageable);

    Project findOne(Long projectId);
}
