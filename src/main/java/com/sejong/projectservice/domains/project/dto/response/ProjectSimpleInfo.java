package com.sejong.projectservice.domains.project.dto.response;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.collaborator.dto.CollaboratorResponse;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.domains.category.dto.CategoryDto;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDto;
import com.sejong.projectservice.domains.techstack.dto.TechStackDto;
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
public class ProjectSimpleInfo {
    private Long id;
    private String title;
    private String description;

    private String username;
    private String ownerNickname;
    private String ownerRealName;

    private ProjectStatus projectStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String thumbnailUrl;

    private List<SubGoalDto> subGoalDtos = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private List<TechStackDto> techStackDtos = new ArrayList<>();
    private List<CollaboratorResponse> collaborators = new ArrayList<>();
    private Integer collaboratorSize;

    public static ProjectSimpleInfo from(ProjectEntity project, Map<String, UserNameInfo> userNameInfos) {

        List<CollaboratorResponse> collaboratorList = project.getCollaboratorEntities().stream()
                .map(collaborator -> {
                    return CollaboratorResponse.of(
                            collaborator.getId(),
                            collaborator.getCollaboratorName(),
                            userNameInfos.get(collaborator.getCollaboratorName()).nickname(),
                            userNameInfos.get(collaborator.getCollaboratorName()).realName());
                }).toList();

        List<CategoryEntity> categoryEntityEntities = project.getProjectCategories().stream()
                .map(ProjectCategoryEntity::getCategoryEntity).toList();

        List<TechStackEntity> techStackEntities = project.getProjectTechStacks().stream()
                .map(ProjectTechStackEntity::getTechStackEntity)
                .toList();

        return ProjectSimpleInfo.builder()
                .id(project.getId())
                .title(project.getTitle())
                .username(project.getUsername())
                .ownerNickname(userNameInfos.get(project.getUsername()).nickname())
                .ownerRealName(userNameInfos.get(project.getUsername()).realName())
                .description(project.getDescription())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .thumbnailUrl(project.getThumbnailUrl())
                .categories(CategoryDto.fromList(categoryEntityEntities))
                .techStackDtos(TechStackDto.fromList(techStackEntities))
                .collaborators(collaboratorList)
                .subGoalDtos(SubGoalDto.toDtoList(project.getSubGoals()))
                .collaboratorSize(collaboratorList.size())
                .build();
    }
}

// 상세 내용 및 하위목표가 없는 객체입니다.