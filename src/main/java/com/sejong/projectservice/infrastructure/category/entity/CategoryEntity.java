package com.sejong.projectservice.infrastructure.category.entity;

import com.sejong.projectservice.core.category.Category;
import com.sejong.projectservice.infrastructure.project_category.entity.ProjectCategoryEntity;
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

    public static CategoryEntity from(Category category) {
        return CategoryEntity.builder()
                .id(null)
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public Category toDomain() {
        return Category.builder()
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
