package com.sejong.projectservice.domains.project.repository;

import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository {

    ProjectDto save(ProjectDto projectDto);

    Page<ProjectDto> findAll(Pageable pageable);

    Page<ProjectDto> searchWithFilters(String keyword, ProjectStatus status, Pageable pageable);

    ProjectDto findOne(Long projectId);

    boolean existsById(Long postId);

    void deleteById(Long projectId);


    ProjectDto updateCollaborator(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    Long getProjectCount();
}
