package com.sejong.projectservice.domains.project.domain;

import com.sejong.projectservice.domains.category.domain.Category;
import com.sejong.projectservice.domains.collaborator.domain.Collaborator;
import com.sejong.projectservice.domains.document.domain.Document;
import com.sejong.projectservice.domains.enums.ProjectStatus;
import com.sejong.projectservice.domains.subgoal.domain.SubGoal;
import com.sejong.projectservice.domains.techstack.domain.TechStack;
import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import com.sejong.projectservice.domains.collaborator.domain.CollaboratorEntity;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String username;

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
    private List<CollaboratorEntity> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubGoalEntity> subGoals = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DocumentEntity> documents = new ArrayList<>();

    public static ProjectEntity from(Project project) {
        return ProjectEntity.builder()
                .title(project.getTitle())
                .description(project.getDescription())
                .username(project.getUsername())
                .projectStatus(project.getProjectStatus())
                .thumbnailUrl(project.getThumbnailUrl())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .projectCategories(new ArrayList<>())
                .projectTechStacks(new ArrayList<>())
                .collaborators(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .documents(new ArrayList<>())
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
        this.collaborators.add(collaboratorEntity);
    }

    public void addSubGoal(SubGoalEntity subGoalEntity) {
        subGoalEntity.assignProjectEntity(this);
        this.subGoals.add(subGoalEntity);
    }

    public void addDocument(DocumentEntity documentEntity) {
        documentEntity.assignDocumentEntity(this);
        this.documents.add(documentEntity);
    }

    public void removeDocument(DocumentEntity documentEntity) {
        documents.remove(documentEntity);
    }

    public void update(Project project) {
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.projectStatus = project.getProjectStatus();
        this.thumbnailUrl = project.getThumbnailUrl();
        this.updatedAt = LocalDateTime.now();
    }

    public Project toDomain() {

        List<Collaborator> collaboratorList = new ArrayList<>(collaborators.stream()
                .map(CollaboratorEntity::toDomain)
                .toList());

        List<Category> categories = new ArrayList<>(projectCategories.stream()
                .map(ProjectCategoryEntity::getCategoryEntity)
                .map(CategoryEntity::toDomain)
                .distinct()
                .toList());

        List<TechStack> uniqueTechStackList = new ArrayList<>(projectTechStacks.stream()
                .map(ProjectTechStackEntity::getTechStackEntity)
                .map(TechStackEntity::toDomain)
                .distinct()
                .toList());

        List<SubGoal> subGoalList = new ArrayList<>(subGoals.stream()
                .map(SubGoalEntity::toDomain)
                .toList());

        List<Document> documentList = new ArrayList<>(documents.stream()
                .map(DocumentEntity::toDomain)
                .toList());

        return Project.builder()
                .id(this.id)
                .title(this.title)
                .username(this.username)
                .description(this.description)
                .projectStatus(this.projectStatus)
                .thumbnailUrl(this.thumbnailUrl)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .categories(categories)
                .collaborators(collaboratorList)
                .techStacks(uniqueTechStackList)
                .subGoals(subGoalList)
                .documents(documentList)
                .build();
    }
}
