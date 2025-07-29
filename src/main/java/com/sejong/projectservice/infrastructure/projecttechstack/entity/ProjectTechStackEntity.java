package com.sejong.projectservice.infrastructure.projecttechstack.entity;

import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
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
@Table(name = "project_techstack")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProjectTechStackEntity {

    @Id
    @Column(name = "project_techstack_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "techstack_id", nullable = false)
    private TechStackEntity techStackEntity;

    public static ProjectTechStackEntity of(ProjectEntity projectEntity, TechStackEntity techStackEntity) {
        return ProjectTechStackEntity.builder()
                .projectEntity(projectEntity)
                .techStackEntity(techStackEntity)
                .build();
    }
}
