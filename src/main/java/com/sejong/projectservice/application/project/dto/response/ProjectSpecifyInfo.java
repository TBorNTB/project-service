package com.sejong.projectservice.application.project.dto.response;

import com.sejong.projectservice.application.collaborator.dto.response.CollaboratorResponse;
import com.sejong.projectservice.client.response.UserNameInfo;
import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.core.document.domain.Document;
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

    private List<SubGoal> subGoals = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<TechStack> techStacks = new ArrayList<>();
    private List<CollaboratorResponse> collaborators = new ArrayList<>();
    private List<Document> documents = new ArrayList<>();

    public static ProjectSpecifyInfo from(Project project, Map<String, UserNameInfo> usernames) {

        List<CollaboratorResponse> collaboratorResponseList = project.getCollaborators().stream()
                .map(collaborator -> {
                    return CollaboratorResponse.of(collaborator.getId(), collaborator.getCollaboratorName(),
                            usernames.get(collaborator.getCollaboratorName()).nickname(),
                            usernames.get(collaborator.getCollaboratorName()).realName());
                }).toList();

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
                .categories(project.getCategories())
                .subGoals(project.getSubGoals())
                .techStacks(project.getTechStacks())
                .collaborators(collaboratorResponseList)
                .documents(project.getDocuments())
                .build();
    }
}
