package com.sejong.projectservice.application.project.dto.response;

import com.sejong.projectservice.core.projectuser.ProjectUser;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
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
public class ProjectSimpleInfo {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private ProjectStatus projectStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String thumbnailUrl;
    private List<TechStack> techStacks = new ArrayList<>();
    private List<ProjectUser> projectUsers = new ArrayList<>();
    private Integer collaboratorSize;

    public static ProjectSimpleInfo from(Project project) {

        List<ProjectUser> projectUserList = project.getProjectUsers();

        return ProjectSimpleInfo.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .category(project.getCategory())
                .projectStatus(project.getProjectStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .thumbnailUrl(project.getThumbnailUrl())
                .techStacks(project.getTechStacks())
                .projectUsers(projectUserList)
                .collaboratorSize(projectUserList.size())
                .build();
    }
}

// 상세 내용 및 하위목표가 없는 객체입니다.