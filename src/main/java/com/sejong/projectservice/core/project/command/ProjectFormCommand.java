package com.sejong.projectservice.core.project.command;

import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ProjectFormCommand {
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