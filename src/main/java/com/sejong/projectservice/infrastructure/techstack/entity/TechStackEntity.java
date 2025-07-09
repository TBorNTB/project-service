package com.sejong.projectservice.infrastructure.techstack.entity;

import com.sejong.projectservice.core.techstack.TechStack;
import jakarta.persistence.*;
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
}
