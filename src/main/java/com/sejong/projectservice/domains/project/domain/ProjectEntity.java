package com.sejong.projectservice.domains.project.domain;

import com.sejong.projectservice.support.common.internal.response.UserNameInfo;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProjectEntity {

    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;
    private String description;

    private String username;
    private String nickname;
    private String realname;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ProjectStatus projectStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectCategoryEntity> projectCategories = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectTechStackEntity> projectTechStacks = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CollaboratorEntity> collaboratorEntities = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubGoalEntity> subGoals = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentEntity> documentEntities = new ArrayList<>();

    public static ProjectEntity from(ProjectDto projectDto) {
        return ProjectEntity.builder()
                .title(projectDto.getTitle())
                .description(projectDto.getDescription())
                .username(projectDto.getUsername())
                .projectStatus(projectDto.getProjectStatus())
                .thumbnailUrl(projectDto.getThumbnailUrl())
                .createdAt(projectDto.getCreatedAt())
                .updatedAt(projectDto.getUpdatedAt())
                .projectCategories(new ArrayList<>())
                .projectTechStacks(new ArrayList<>())
                .collaboratorEntities(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .documentEntities(new ArrayList<>())
                .build();
    }

    public static ProjectEntity of(ProjectFormRequest request, String username, UserNameInfo userNameInfo) {
        return ProjectEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .username(username)
                .realname(userNameInfo.realName())
                .nickname(userNameInfo.nickname())
                .projectStatus(request.getProjectStatus())
                .thumbnailUrl(request.getThumbnail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectCategories(new ArrayList<>())
                .projectTechStacks(new ArrayList<>())
                .collaboratorEntities(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .documentEntities(new ArrayList<>())
                .build();
    }

    public void addCategory(CategoryEntity categoryEntity) {
        ProjectCategoryEntity link = ProjectCategoryEntity.of(this, categoryEntity);
        categoryEntity.addProjectCategoryEntity(link);
        this.projectCategories.add(link);
    }

    public void addTechStack(TechStackEntity techStackEntity) {
        ProjectTechStackEntity link = ProjectTechStackEntity.of(this, techStackEntity);
        techStackEntity.addProjectTechStackEntity(link);
        this.projectTechStacks.add(link);
    }

    public void addCollaborator(CollaboratorEntity collaboratorEntity) {
        collaboratorEntity.assignProjectEntity(this);
        this.collaboratorEntities.add(collaboratorEntity);
    }

    public void addSubGoal(SubGoalEntity subGoalEntity) {
        subGoalEntity.assignProjectEntity(this);
        this.subGoals.add(subGoalEntity);
    }

    public void addDocument(DocumentEntity documentEntity) {
        documentEntity.assignDocumentEntity(this);
        this.documentEntities.add(documentEntity);
    }

    public void removeDocument(DocumentEntity documentEntity) {
        documentEntities.remove(documentEntity);
    }

    public void update(ProjectDto projectDto) {
        this.title = projectDto.getTitle();
        this.description = projectDto.getDescription();
        this.projectStatus = projectDto.getProjectStatus();
        this.thumbnailUrl = projectDto.getThumbnailUrl();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description,
                       ProjectStatus projectStatus, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.updatedAt = LocalDateTime.now();
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


    public boolean ensureCollaboratorExists(String userName) {
        boolean exists = collaboratorEntities.stream()
                .anyMatch(collaborator -> collaborator.getCollaboratorName().equals(userName));

        return exists;
    }

    public void checkSubGoal(Long subGoalId) {
        SubGoalEntity selectedSubGaol = subGoals.stream()
                .filter(subGoal -> subGoal.getId().equals(subGoalId))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 subGoalId는 관련 프로젝트 내에 없습니다."));

        selectedSubGaol.check();
    }

    public void updateCategory(List<String> categoryNames,  List<CategoryEntity> categoryEntityEntities1) {
        this.projectCategories.clear(); // orphanRemoval로 기존 링크 전부 삭제
        categoryEntityEntities1.forEach(this::addCategory);
    }

    public void updateCollaborator(List<String> collaboratorNames) {
        List<String> uniqueNames = collaboratorNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        this.collaboratorEntities.clear();

        for (String name : uniqueNames) {
          CollaboratorEntity.of(name, this);
        }
    }
}
