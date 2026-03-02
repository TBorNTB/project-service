package com.sejong.projectservice.domains.project.domain;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private String username;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ProjectStatus projectStatus;


    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String thumbnailKey;

    @Column(name = "parent_project_id")
    private Long parentProjectId;

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProjectCategoryEntity> projectCategories = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProjectTechStackEntity> projectTechStacks = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CollaboratorEntity> collaboratorEntities = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SubGoalEntity> subGoals = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DocumentEntity> documentEntities = new ArrayList<>();

    public static ProjectEntity of(ProjectFormRequest request, String username) {
        return ProjectEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .username(username)
                .projectStatus(request.getProjectStatus())
                .thumbnailKey(request.getThumbnail())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .startedAt(request.getStartedAt())
                .updatedAt(LocalDateTime.now())
                .endedAt(request.getEndedAt())
                .parentProjectId(request.getParentProjectId())
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

    public void removeSubGoal(SubGoalEntity subGoalEntity) {
        subGoals.remove(subGoalEntity);
    }

    public void update(String title, String description,
                       ProjectStatus projectStatus, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
        this.thumbnailKey = thumbnailUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void validateUserPermission(String username) {
        if (this.username.equals(username)) {
            return;
        }

        boolean exists = ensureCollaboratorExists(username);
        if (exists == false) {
            throw new BaseException(ExceptionType.FORBIDDEN);
        }
    }

    public void validateOwner(String username, String userRole) {
        if (!this.username.equals(username) && !userRole.equalsIgnoreCase("ADMIN")) {
            throw new BaseException(ExceptionType.REQUIRED_ADMIN);
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
                .orElseThrow(() -> new BaseException(ExceptionType.SUBGOAL_NOT_FOUND));

        selectedSubGaol.check();
    }

    public void updateCategory(List<CategoryEntity> categories) {
        this.projectCategories.clear(); // orphanRemoval로 기존 링크 전부 삭제
        categories.forEach(this::addCategory);
    }

    public void updateTechStack(List<TechStackEntity> techStacks) {
        this.projectTechStacks.clear();
        techStacks.forEach(this::addTechStack);
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

    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateThumbnail(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
        this.updatedAt = LocalDateTime.now();
    }
}
