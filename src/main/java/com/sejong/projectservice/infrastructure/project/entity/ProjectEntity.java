package com.sejong.projectservice.infrastructure.project.entity;

import com.sejong.projectservice.core.collaborator.Collaborator;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.infrastructure.collborator.entity.CollaboratorEntity;
import com.sejong.projectservice.infrastructure.document.entity.DocumentEntity;
import com.sejong.projectservice.infrastructure.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.infrastructure.subgoal.SubGoalEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "VARCHAR(50)")
  private Category category;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "VARCHAR(50)")
  private ProjectStatus projectStatus;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private String thumbnailUrl;

  @Column(columnDefinition = "TEXT")
  private String contentJson;

  @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProjectTechStackEntity> projectTechStacks = new ArrayList<>();

  @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CollaboratorEntity> collaborators = new ArrayList<>();

  @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SubGoalEntity> subGoals = new ArrayList<>();

  @OneToMany(mappedBy = "projectEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DocumentEntity> documents = new ArrayList<>();

  public static ProjectEntity from(Project project) {
    return ProjectEntity.builder()
        .title(project.getTitle())
        .description(project.getDescription())
        .category(project.getCategory())
        .projectStatus(project.getProjectStatus())
        .thumbnailUrl(project.getThumbnailUrl())
        .createdAt(project.getCreatedAt())
        .updatedAt(project.getUpdatedAt())
        .projectTechStacks(new ArrayList<>())
        .collaborators(new ArrayList<>())
        .subGoals(new ArrayList<>())
        .build();
  }

  // createdAt만 제외했습니다.
  public void updateBasicInfo(Project project) {
    clearAllRelations();
    this.title = project.getTitle();
    this.description = project.getDescription();
    this.category = project.getCategory();
    this.projectStatus = project.getProjectStatus();
    this.updatedAt = project.getUpdatedAt();
    this.thumbnailUrl = project.getThumbnailUrl();
  }

  public void clearAllRelations() {
    this.projectTechStacks.clear();
    this.collaborators.clear();
    this.subGoals.clear();
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
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt)
        .collaborators(collaboratorList)
        .techStacks(uniqueTechStackList)
        .subGoals(subGoalList)
        .build();
  }
}
