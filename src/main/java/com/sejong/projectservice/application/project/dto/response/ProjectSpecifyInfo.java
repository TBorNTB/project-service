package com.sejong.projectservice.application.project.dto.response;

import com.sejong.projectservice.core.projectuser.ProjectUser;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectSpecifyInfo {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private ProjectStatus projectStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String thumbnailUrl;
    private String contentJson;
    private List<SubGoal> subGoals;
    private List<TechStack> techStacks = new ArrayList<>();
    private List<ProjectUser> projectUsers = new ArrayList<>();

    public static ProjectSpecifyInfo from(Project project) {
        return ProjectSpecifyInfo.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .category(project.getCategory())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .thumbnailUrl(project.getThumbnailUrl())
                .contentJson(project.getContentJson())
                .subGoals(project.getSubGoals())
                .techStacks(project.getTechStacks())
                .projectUsers(project.getProjectUsers())
                .build();
    }
}
