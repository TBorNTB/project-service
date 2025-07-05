package com.sejong.projectservice.infrastructure.collborator.entity;

import com.sejong.projectservice.core.collaborator.Collaborator;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="collaborator")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable=false)
    private ProjectEntity projectEntity;

    private String collaboratorName;

    public static CollaboratorEntity from(Collaborator collaborator , ProjectEntity projectEntity) {

        CollaboratorEntity entity = CollaboratorEntity.builder()
                .collaboratorName(collaborator.getCollaboratorName())
                .projectEntity(projectEntity)
                .build();

        entity.assignProjectEntity(projectEntity);
        return entity;
    }

    public Collaborator toDomain() {
        return Collaborator.builder()
                .id(this.getId())
                .collaboratorName(this.getCollaboratorName())
                .build();
    }

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
        projectEntity.getCollaborators().add(this);
    }

    public static CollaboratorEntity withoutProject(Collaborator collaborator) {
        return CollaboratorEntity.builder()
                .id(collaborator.getId())
                .collaboratorName(collaborator.getCollaboratorName())
                .build();
    }
}
