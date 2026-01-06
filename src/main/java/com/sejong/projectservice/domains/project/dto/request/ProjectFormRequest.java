package com.sejong.projectservice.domains.project.dto.request;

import com.sejong.projectservice.support.common.constants.ProjectStatus;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectFormRequest {

    private String title;
    private String description;
    private String thumbnail;
    private String content;

    private ProjectStatus projectStatus;

    private List<String> categories;
    private List<String> collaborators;
    private List<String> techStacks;
    private List<String> subGoals;

    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
}
