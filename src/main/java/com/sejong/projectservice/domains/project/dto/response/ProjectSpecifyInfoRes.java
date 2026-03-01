package com.sejong.projectservice.domains.project.dto.response;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.category.dto.CategoryDto;
import com.sejong.projectservice.domains.collaborator.dto.CollaboratorResponse;
import com.sejong.projectservice.domains.document.dto.DocumentDto;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDto;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.domains.techstack.dto.TechStackDto;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.support.common.internal.response.UserProfileDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectSpecifyInfoRes {

    private Long id;
    private Long parentProjectId;
    private String title;
    private String description;
    private UserProfileDto ownerProfile;

    private ProjectStatus projectStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime endedAt;

    private String thumbnailUrl;
    private String content;

    private List<SubGoalDto> subGoalDtos = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private List<TechStackDto> techStackDtos = new ArrayList<>();
    private List<CollaboratorResponse> collaborators = new ArrayList<>();
    private List<DocumentDto> documentDtos = new ArrayList<>();

    public static ProjectSpecifyInfoRes from(ProjectEntity project, Map<String, UserNameInfo> usernames,
                                             FileUploader fileUploader) {

        List<CollaboratorResponse> collaboratorResponseList = project.getCollaboratorEntities().stream()
                .map(collaborator -> CollaboratorResponse.of(
                        collaborator.getId(),
                        UserProfileDto.from(collaborator.getCollaboratorName(),
                                usernames.get(collaborator.getCollaboratorName()))))
                .toList();

        List<CategoryEntity> categoryEntityEntities = project.getProjectCategories().stream()
                .map(ProjectCategoryEntity::getCategoryEntity).toList();

        List<TechStackEntity> techStackEntities = project.getProjectTechStacks().stream()
                .map(ProjectTechStackEntity::getTechStackEntity)
                .toList();

        String thumbnailUrl = project.getThumbnailKey() != null
                ? fileUploader.getFileUrl(project.getThumbnailKey())
                : null;

        return ProjectSpecifyInfoRes.builder()
                .id(project.getId())
                .parentProjectId(project.getParentProjectId())
                .title(project.getTitle())
                .ownerProfile(UserProfileDto.from(project.getUsername(), usernames.get(project.getUsername())))
                .description(project.getDescription())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .endedAt(project.getEndedAt())
                .thumbnailUrl(thumbnailUrl)
                .content(project.getContent())
                .categories(CategoryDto.fromList(categoryEntityEntities))
                .subGoalDtos(SubGoalDto.toDtoList(project.getSubGoals()))
                .techStackDtos(TechStackDto.fromList(techStackEntities))
                .collaborators(collaboratorResponseList)
                .documentDtos(DocumentDto.from(project.getDocumentEntities(), fileUploader))
                .build();
    }
}
