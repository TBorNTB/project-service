package com.sejong.projectservice.infrastructure.projecttechstack.entity;

import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="project_techstack")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProjectTechStackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable=false)
    private ProjectEntity projectEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="techstack_id",nullable=false)
    private TechStackEntity techStackEntity;

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
        projectEntity.getProjectTechStacks().add(this);
    }

    public static ProjectTechStackEntity from(ProjectEntity projectEntity, TechStackEntity techStackEntity) {
        ProjectTechStackEntity projectTechStackEntity = ProjectTechStackEntity.builder()
                .projectEntity(projectEntity)
                .techStackEntity(techStackEntity)
                .build();

        projectTechStackEntity.assignProjectEntity(projectEntity);
        return projectTechStackEntity;
    }
}
