package com.sejong.projectservice.domains.project.kafka;

import com.sejong.projectservice.domains.category.domain.CategoryDto;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectDto;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectEvent {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private String id;

    private String title;
    private String description;

    private ProjectStatus projectStatus;

    private String createdAt;
    private String updatedAt;

    private String thumbnailUrl;

    private List<String> projectCategories = new ArrayList<>();

    private List<String> projectTechStacks = new ArrayList<>();

    private List<String> collaborators = new ArrayList<>();

    public static ProjectEvent from(ProjectDto projectDto){

        List<String> categoryNames = projectDto.getCategories().stream()
                .map(CategoryDto::getName)
                .distinct()
                .toList();

        List<String> techStackNames = projectDto.getTechStackDtos().stream()
                .map(TechStackDto::getName)
                .distinct()
                .toList();

        List<String> collaboratorNames = projectDto.getCollaboratorDtos().stream()
                .map(CollaboratorDto::getCollaboratorName)
                .distinct()
                .toList();

        return ProjectEvent.builder()
                .id(projectDto.getId().toString())
                .title(projectDto.getTitle())
                .description(projectDto.getDescription())
                .thumbnailUrl(projectDto.getThumbnailUrl())
                .projectStatus(projectDto.getProjectStatus())
                .createdAt(projectDto.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(projectDto.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .projectCategories(categoryNames)
                .projectTechStacks(techStackNames)
                .collaborators(collaboratorNames)
                .build();
    }

    public static ProjectEvent from2(ProjectEntity project) {

        List<String> categoryNames = project.getProjectCategories().stream()
                .map(it -> {
                    return it.getCategoryEntity().getName();
                })
                .distinct()
                .toList();

        List<String> techStackNames = project.getProjectTechStacks().stream()
                .map(it -> {
                    return it.getTechStackEntity().getName();
                })
                .distinct()
                .toList();

        List<String> collaboratorNames = project.getCollaboratorEntities().stream()
                .map(CollaboratorEntity::getCollaboratorName)
                .distinct()
                .toList();

        return ProjectEvent.builder()
                .id(project.getId().toString())
                .title(project.getTitle())
                .description(project.getDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(project.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .projectCategories(categoryNames)
                .projectTechStacks(techStackNames)
                .collaborators(collaboratorNames)
                .build();
    }
}
