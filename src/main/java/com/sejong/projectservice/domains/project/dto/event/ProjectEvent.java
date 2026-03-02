package com.sejong.projectservice.domains.project.dto.event;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.support.common.file.FileUploader;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String startedAt;
    private String endedAt;
    private String createdAt;
    private String updatedAt;

    private String thumbnailUrl;

    private List<String> projectCategories = new ArrayList<>();

    private List<String> projectTechStacks = new ArrayList<>();

    private String username;
    private List<String> collaborators = new ArrayList<>();
    
    public static ProjectEvent from(ProjectEntity project, FileUploader fileUploader) {
        String thumbnailUrl = project.getThumbnailKey() != null
                ? fileUploader.getFileUrl(project.getThumbnailKey())
                : null;

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
                .thumbnailUrl(thumbnailUrl)
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .updatedAt(project.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .startedAt(project.getStartedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .endedAt(project.getEndedAt().truncatedTo(ChronoUnit.MILLIS).format(FORMATTER))
                .projectCategories(categoryNames)
                .projectTechStacks(techStackNames)
                .username(project.getUsername())
                .collaborators(collaboratorNames)
                .build();
    }
}
