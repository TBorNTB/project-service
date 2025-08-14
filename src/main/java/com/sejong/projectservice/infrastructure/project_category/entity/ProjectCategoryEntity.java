package com.sejong.projectservice.infrastructure.project_category.entity;

import com.sejong.projectservice.infrastructure.category.entity.CategoryEntity;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
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

import java.util.Objects;

@Entity
@Table(name = "project_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProjectCategoryEntity {

    @Id
    @Column(name = "project_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    public static ProjectCategoryEntity of(ProjectEntity projectEntity, CategoryEntity categoryEntity) {
        return ProjectCategoryEntity.builder()
                .projectEntity(projectEntity)
                .categoryEntity(categoryEntity)
                .build();
    }
}
