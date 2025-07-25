package com.sejong.projectservice.infrastructure.projectuser.entity;

import com.sejong.projectservice.core.projectuser.ProjectUser;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="projectuser")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable=false)
    private ProjectEntity projectEntity;

    private String collaboratorName;

    public static ProjectUserEntity from(ProjectUser projectUser, ProjectEntity projectEntity) {

        ProjectUserEntity entity = ProjectUserEntity.builder()
                .collaboratorName(projectUser.getCollaboratorName())
                .projectEntity(projectEntity)
                .build();

        entity.assignProjectEntity(projectEntity);
        return entity;
    }

    public ProjectUser toDomain() {
        return ProjectUser.builder()
                .id(this.getId())
                .collaboratorName(this.getCollaboratorName())
                .build();
    }

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
        projectEntity.getCollaborators().add(this);
    }

    public static ProjectUserEntity withoutProject(ProjectUser projectUser) {
        return ProjectUserEntity.builder()
                .id(projectUser.getId())
                .collaboratorName(projectUser.getCollaboratorName())
                .build();
    }
}
