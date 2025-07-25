package com.sejong.projectservice.core.project.repository;

import com.sejong.projectservice.core.common.PageResult;
import com.sejong.projectservice.core.common.PageSearchCommand;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;

public interface ProjectRepository {

    Project save(Project project);

    PageResult<Project> findAll(PageSearchCommand pageSearchCommand);

    Project update(Project project, Long projectId);

    PageResult<Project> searchWithFilters(String keyword, Category category, ProjectStatus status, PageSearchCommand pageSearchCommand);

    Project findOne(Long projectId);
}
