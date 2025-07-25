package com.sejong.projectservice.core.assembler;

import com.sejong.projectservice.core.project.command.ProjectFormCommand;
import com.sejong.projectservice.core.projectuser.ProjectUser;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectAssembler {

    public static Project toDomain(ProjectFormCommand command, Long userId) {
        List<ProjectUser> projectUsers = command.getCollaborators().stream()
                .map(ProjectUser::from)
                .toList();

        List<TechStack> techStacks = command.getTechStacks().stream()
                .map(TechStack::of)
                .toList();

        List<SubGoal> subGoals = command.getSubGoals().stream()
                .map(it -> SubGoal.from(it, false, LocalDateTime.now(), LocalDateTime.now()))
                .toList();

        return Project.builder()
                .title(command.getTitle())
                .description(command.getDescription())
                .category(command.getCategory())
                .projectStatus(command.getProjectStatus())
                .createdAt(command.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .thumbnailUrl(command.getThumbnail())
                .contentJson(command.getContentJson())
                .userId(userId)
                .techStacks(techStacks)
                .projectUsers(projectUsers)
                .subGoals(subGoals)
                .build();
    }
}