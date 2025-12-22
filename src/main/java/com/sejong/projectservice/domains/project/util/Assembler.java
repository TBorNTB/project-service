package com.sejong.projectservice.domains.project.util;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.document.domain.DocumentDto;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.domains.category.domain.CategoryDto;
import com.sejong.projectservice.domains.project.domain.ProjectDto;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.domains.techstack.domain.TechStackDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Assembler {
    public static DocumentDto toDocument(DocumentCreateReq request, String yorkieDocumentId, Long projectId) {
        return DocumentDto.builder()
                .id(null)
                .yorkieDocumentId(yorkieDocumentId)
                .title(request.getTitle())
                .content(request.getContent())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectId(projectId)
                .build();
    }

    public static ProjectEntity toProjectEntity(ProjectFormRequest request, String username, UserNameInfo userNameInfo) {
        ProjectEntity projectEntity = ProjectEntity.of(request,username,userNameInfo);

        return projectEntity;
    }

    public static ProjectDto toProject(ProjectFormRequest request, String username, UserNameInfo userNameInfo) {
        List<CollaboratorDto> collaboratorDtos = request.getCollaborators().stream()
                .map(CollaboratorDto::from)
                .toList();

        List<TechStackDto> techStackDtos = request.getTechStacks().stream()
                .map(TechStackDto::of)
                .toList();

        List<CategoryDto> categories = request.getCategories().stream()
                .map(CategoryDto::of)
                .toList();

        List<SubGoalDto> subGoalDtos = request.getSubGoals().stream()
                .map(it -> SubGoalDto.from(it, false, LocalDateTime.now(), LocalDateTime.now()))
                .toList();

        return ProjectDto.builder()
                .title(request.getTitle())
                .username(username)
                .nickname(userNameInfo.nickname())
                .realname(userNameInfo.nickname())
                .description(request.getDescription())
                .categories(categories)
                .projectStatus(request.getProjectStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .thumbnailUrl(request.getThumbnail())
                .techStackDtos(techStackDtos)
                .collaboratorDtos(collaboratorDtos)
                .subGoalDtos(subGoalDtos)
                .documentDtos(new ArrayList<>())
                .build();
    }
}
