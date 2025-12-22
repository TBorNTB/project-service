package com.sejong.projectservice.domains.techstack.domain;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.projecttechstack.entity.ProjectTechStackEntity;
import com.sejong.projectservice.domains.techstack.dto.TechStackCreateReq;
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
import java.util.Objects;

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
    @Column(name = "techstack_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "techStackEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTechStackEntity> projectTechStacks = new ArrayList<>();

    public static TechStackEntity of(String name) {
        return TechStackEntity.builder()
                .id(null)
                .name(name)
                .projectTechStacks(new ArrayList<>())
                .build();
    }

    public static TechStackEntity from(TechStackCreateReq techstackCreateReq) {
        return TechStackEntity.builder()
                .id(null)
                .name(techstackCreateReq.getName())
                .build();
    }

    public void addProjectTechStackEntity(ProjectTechStackEntity ptse) {
        this.projectTechStacks.add(ptse);
    }

    public void update(String newName) {
        this.name = newName;
    }

    public void update(TechStackDto techStackDto) {
        this.name = techStackDto.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TechStackEntity that = (TechStackEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
