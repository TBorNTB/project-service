package com.sejong.projectservice.infrastructure.collborator.entity;

import com.sejong.projectservice.core.collaborator.Collaborator;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "collaborator")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    private String collaboratorName;

    public static CollaboratorEntity from(Collaborator collaborator) {
        return CollaboratorEntity.builder()
                .collaboratorName(collaborator.getCollaboratorName())
                .build();
    }

    public Collaborator toDomain() {
        return Collaborator.builder()
                .id(this.getId())
                .collaboratorName(this.getCollaboratorName())
                .build();
    }

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }
}
