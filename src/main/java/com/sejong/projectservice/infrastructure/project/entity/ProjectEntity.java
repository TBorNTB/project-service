package com.sejong.projectservice.infrastructure.project.entity;

import com.sejong.projectservice.core.collaborator.Collaborator;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.infrastructure.collborator.entity.CollaboratorEntity;
import com.sejong.projectservice.infrastructure.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.infrastructure.subgoal.SubGoalEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ProjectStatus projectStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long userId;

    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String contentJson;

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTechStackEntity> projectTechStacks = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollaboratorEntity> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubGoalEntity> subGoals = new ArrayList<>();

    // createdAt만 제외했습니다.
    public void updateBasicInfo(Project project){
        clearAllRelations();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.category = project.getCategory();
        this.projectStatus = project.getProjectStatus();
        this.updatedAt = project.getUpdatedAt();
        this.thumbnailUrl = project.getThumbnailUrl();
        this.contentJson = project.getContentJson();
    }

    public void clearAllRelations() {
        this.projectTechStacks.clear();
        this.collaborators.clear();
        this.subGoals.clear();
    }

    public static ProjectEntity from(Project project) {
        return ProjectEntity.builder()
                .title(project.getTitle())
                .description(project.getDescription())
                .category(project.getCategory())
                .projectStatus(project.getProjectStatus())
                .thumbnailUrl(project.getThumbnailUrl())
                .contentJson(project.getContentJson())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .projectTechStacks(new ArrayList<>())
                .collaborators(new ArrayList<>())
                .subGoals(new ArrayList<>())
                .userId(project.getUserId())
                .build();
    }

    public Project toDomain() {

        List<Collaborator> collaboratorList = collaborators.stream()
                .map(CollaboratorEntity::toDomain)
                .toList();
        List<TechStack> uniqueTechStackList = projectTechStacks.stream()
                .map(ProjectTechStackEntity::getTechStackEntity)
                .map(TechStackEntity::toDomain)
                .distinct()
                .toList();

        List<SubGoal> subGoalList = subGoals.stream()
                .map(SubGoalEntity::toDomain)
                .toList();

        return Project.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .category(this.category)
                .projectStatus(this.projectStatus)
                .thumbnailUrl(this.thumbnailUrl)
                .contentJson(this.contentJson)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .collaborators(collaboratorList)
                .techStacks(uniqueTechStackList)
                .subGoals(subGoalList)
                .userId(this.userId)
                .build();
    }
}
