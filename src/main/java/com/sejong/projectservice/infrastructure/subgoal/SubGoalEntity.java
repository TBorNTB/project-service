package com.sejong.projectservice.infrastructure.subgoal;

import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subgoal")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SubGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Boolean completed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    public static SubGoalEntity from(SubGoal subGoal , ProjectEntity projectEntity) {
        SubGoalEntity subGoalEntity = SubGoalEntity.builder()
                .id(null)
                .content(subGoal.getContent())
                .completed(subGoal.getCompleted())
                .createdAt(subGoal.getCreatedAt())
                .updatedAt(subGoal.getUpdatedAt())
                .build();

        subGoalEntity.assignProjectEntity(projectEntity);
        return subGoalEntity;
    }

    public SubGoal toDomain() {
        return SubGoal.builder()
                .id(id)
                .content(content)
                .completed(completed)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
        projectEntity.getSubGoals().add(this);
    }
}
