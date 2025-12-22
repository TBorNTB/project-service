package com.sejong.projectservice.domains.category.domain;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.entity.ProjectCategoryEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCategoryEntity> projectCategories = new ArrayList<>();

    public static CategoryEntity of(String name) {
        return CategoryEntity.builder()
                .id(null)
                .name(name)
                .projectCategories(new ArrayList<>())
                .build();
    }

    public static CategoryEntity from(CategoryDto categoryDto) {
        return CategoryEntity.builder()
                .id(null)
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();
    }

    public static CategoryEntity from2(String categoryName, ProjectEntity projectEntity) {
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(null)
                .name(categoryName)
                .projectCategories(new ArrayList<>())
                .build();
        projectEntity.addCategory(categoryEntity);
        return categoryEntity;
    }

    public CategoryDto toDomain() {
        return CategoryDto.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .build();
    }

    public void addProjectCategoryEntity(ProjectCategoryEntity pce) {
        this.projectCategories.add(pce);
    }

    public void updateName(String nextName) {
        this.name = nextName;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
