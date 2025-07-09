package com.sejong.projectservice.application.project.dto.request;

import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectFormRequest {
    private String title;
    private LocalDateTime createdAt;
    private String description;

    private Category category;
    private ProjectStatus projectStatus;
    private List<String> collaborators;
    private List<String> techStacks;
    private List<String> subGoals;

    private String thumbnail;
    private String contentJson;

}
