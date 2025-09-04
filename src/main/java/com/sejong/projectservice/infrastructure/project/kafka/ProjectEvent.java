package com.sejong.projectservice.infrastructure.project.kafka;

import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.techstack.TechStack;
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

    public static ProjectEvent from(Project project){

        List<String> categoryNames = project.getCategories().stream()
                .map(Category::getName)
                .distinct()
                .toList();

        List<String> techStackNames = project.getTechStacks().stream()
                .map(TechStack::getName)
                .distinct()
                .toList();

        List<String> collaboratorNames = project.getCollaborators().stream()
                .map(Collaborator::getCollaboratorName)
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
