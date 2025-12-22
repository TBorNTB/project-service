package com.sejong.projectservice.domains.project.domain;

import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.document.domain.DocumentDto;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.category.domain.CategoryDto;
import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.domains.techstack.domain.TechStackDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private Long id;
    private String username;
    private String nickname;
    private String realname;
    private List<CollaboratorDto> collaboratorDtos = new ArrayList<>();

    private String title;
    private String description;
    private ProjectStatus projectStatus;
    private String thumbnailUrl;

    private List<SubGoalDto> subGoalDtos = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();
    private List<TechStackDto> techStackDtos = new ArrayList<>();
    private List<DocumentDto> documentDtos = new ArrayList<>();


    public void update(String title, String description,
                       ProjectStatus projectStatus, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void addDocument(DocumentDto doc) {
        documentDtos.add(doc);
    }

    public void validateUserPermission(String username) {
        if (this.username.equals(username)) {
            return;
        }

        boolean exists = ensureCollaboratorExists(username);
        if (exists == false) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "해당 유저는 프로젝트 접근 권한이 없습니다.");
        }
    }

    public void validateOwner(String username, String userRole) {
        if (!this.username.equals(username) && !userRole.equalsIgnoreCase("ADMIN")) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "해당 유저는 프로젝트 Owner가 아닙니다.");
        }
    }

    public void updateCollaborator(List<String> collaboratorNames) {
        List<CollaboratorDto> collaboratorDtoList = collaboratorNames.stream()
                .map(CollaboratorDto::from)
                .distinct()
                .toList();

        this.collaboratorDtos.clear();
        this.collaboratorDtos.addAll(collaboratorDtoList);
    }

    public boolean ensureCollaboratorExists(String userName) {
        boolean exists = collaboratorDtos.stream()
                .anyMatch(collaborator -> collaborator.getCollaboratorName().equals(userName));

        return exists;
    }

    public void checkSubGoal(Long subGoalId) {
        SubGoalDto selectedSubGaol = subGoalDtos.stream()
                .filter(subGoal -> subGoal.getId().equals(subGoalId))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 subGoalId는 관련 프로젝트 내에 없습니다."));

        selectedSubGaol.check();
    }

    public void updateCategory(List<String> categoryNames) {
        List<CategoryDto> categoriesList = categoryNames.stream()
                .map(CategoryDto::of)
                .distinct()
                .toList();

        this.categories.clear();
        this.categories.addAll(categoriesList);
    }
}
