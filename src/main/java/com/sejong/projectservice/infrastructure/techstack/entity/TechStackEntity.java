package com.sejong.projectservice.infrastructure.techstack.entity;

import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.infrastructure.projecttechstack.entity.ProjectTechStackEntity;
import jakarta.persistence.CascadeType;
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
@Table(name = "techstack")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TechStackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "techStackEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTechStackEntity> projectTechStacks = new ArrayList<>();

    public static TechStackEntity of(String name) {
        return TechStackEntity.builder()
                .id(null)
                .name(name)
                .build();
    }

    public static TechStackEntity from(TechStack techStack) {
        return TechStackEntity.builder()
                .id(null)
                .name(techStack.getName())
                .build();
    }

    public TechStack toDomain() {
        return TechStack.builder()
                .id(this.getId())
                .name(this.getName())
                .build();
    }

    public void addProjectTechStackEntity(ProjectTechStackEntity ptse) {
        projectTechStacks.add(ptse);
    }
}
