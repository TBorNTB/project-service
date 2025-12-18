package com.sejong.projectservice.application.project.dto.response;

import com.sejong.projectservice.application.collaborator.dto.response.CollaboratorResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
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

    private List<SubGoal> subGoals = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<TechStack> techStacks = new ArrayList<>();
    private List<CollaboratorResponse> collaborators = new ArrayList<>();
    private Integer collaboratorSize;

    public static ProjectSimpleInfo from(Project project, Map<String, UserNameInfo> userNameInfos) {

        List<CollaboratorResponse> collaboratorList = project.getCollaborators().stream()
                .map(collaborator -> {
                    return CollaboratorResponse.of(
                            collaborator.getId(),
                            collaborator.getCollaboratorName(),
                            userNameInfos.get(collaborator.getCollaboratorName()).nickname(),
                            userNameInfos.get(collaborator.getCollaboratorName()).realName());
                }).toList();

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
                .categories(project.getCategories())
                .techStacks(project.getTechStacks())
                .collaborators(collaboratorList)
                .subGoals(project.getSubGoals())
                .collaboratorSize(collaboratorList.size())
                .build();
    }
}

// 상세 내용 및 하위목표가 없는 객체입니다.