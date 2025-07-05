package com.sejong.projectservice.core.project.domain;

import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.core.collaborator.Collaborator;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private ProjectStatus projectStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String thumbnailUrl;
    private String contentJson;
    private Long userId;
    private List<SubGoal> subGoals =new ArrayList<>();
    private List<TechStack> techStacks = new ArrayList<>();
    private List<Collaborator> collaborators = new ArrayList<>();

}
