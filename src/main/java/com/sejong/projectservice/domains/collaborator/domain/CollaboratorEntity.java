package com.sejong.projectservice.domains.collaborator.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import jakarta.persistence.Column;
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
@Table(name = "project_user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    private String collaboratorName;

    public static CollaboratorEntity of(String name, ProjectEntity projectEntity) {

        CollaboratorEntity collaboratorEntity = CollaboratorEntity.builder()
                .collaboratorName(name)
                .build();

        projectEntity.addCollaborator(collaboratorEntity);
        return collaboratorEntity;
    }

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }
}
