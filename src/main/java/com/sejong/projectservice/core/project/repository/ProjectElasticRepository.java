package com.sejong.projectservice.core.project.repository;

import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.domain.ProjectDoc;

import java.util.List;

public interface ProjectElasticRepository {
    String save(Project savedProject);

    void deleteById(String projectId);

    List<String> getSuggestions(String query);

    List<ProjectDoc> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, int size, int page);
}
