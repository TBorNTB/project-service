package com.sejong.projectservice.domains.project.dto.response;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.collaborator.dto.CollaboratorResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.domains.category.domain.CategoryDto;
import com.sejong.projectservice.domains.document.domain.DocumentDto;
import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.domains.techstack.domain.TechStackDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectSpecifyInfo {

    private Long id;
    private String title;
    private String description;
    private String username;
    private String ownerNickname;
    private String ownerRealname;

    private ProjectStatus projectStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String thumbnailUrl;
    private String contentJson;

    private List<SubGoalDto> subGoalDtos = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private List<TechStackDto> techStackDtos = new ArrayList<>();
    private List<CollaboratorResponse> collaborators = new ArrayList<>();
    private List<DocumentDto> documentDtos = new ArrayList<>();

    public static ProjectSpecifyInfo from(ProjectEntity project, Map<String, UserNameInfo> usernames) {

        List<CollaboratorResponse> collaboratorResponseList = project.getCollaboratorEntities().stream()
                .map(collaborator -> {
                    return CollaboratorResponse.of(collaborator.getId(), collaborator.getCollaboratorName(),
                            usernames.get(collaborator.getCollaboratorName()).nickname(),
                            usernames.get(collaborator.getCollaboratorName()).realName());
                }).toList();


        List<CategoryEntity> categoryEntityEntities = project.getProjectCategories().stream()
                .map(ProjectCategoryEntity::getCategoryEntity).toList();

        List<TechStackEntity> techStackEntities = project.getProjectTechStacks().stream()
                .map(ProjectTechStackEntity::getTechStackEntity)
                .toList();

        return ProjectSpecifyInfo.builder()
                .id(project.getId())
                .title(project.getTitle())
                .username(project.getUsername())
                .ownerNickname(usernames.get(project.getUsername()).nickname())
                .ownerRealname(usernames.get(project.getUsername()).realName())
                .description(project.getDescription())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .thumbnailUrl(project.getThumbnailUrl())
                .categories(CategoryDto.from2(categoryEntityEntities))
                .subGoalDtos(SubGoalDto.toDtoList(project.getSubGoals()))
                .techStackDtos(TechStackDto.from2(techStackEntities))
                .collaborators(collaboratorResponseList)
                .documentDtos(DocumentDto.from2(project.getDocuments()))
                .build();
    }
}
