package com.sejong.projectservice.core.project.repository;

import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.project.kafka.ProjectDocument;

import java.util.List;

public interface ProjectElasticRepository {
    String save(Project savedProject);

    void deleteById(String projectId);

    List<String> getSuggestions(String query);

    List<ProjectDocument> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, int size, int page);

}
