package com.sejong.projectservice.application.mapper;

import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.core.project.command.ProjectFormCommand;

public class ProjectCommandMapper {

    public static ProjectFormCommand toCommand(ProjectFormRequest request) {
        return ProjectFormCommand.builder()
                .title(request.getTitle())
                .createdAt(request.getCreatedAt())
                .description(request.getDescription())
                .category(request.getCategory())
                .projectStatus(request.getProjectStatus())
                .collaborators(request.getCollaborators())
                .techStacks(request.getTechStacks())
                .subGoals(request.getSubGoals())
                .thumbnail(request.getThumbnail())
                .contentJson(request.getContentJson())
                .build();
    }
}