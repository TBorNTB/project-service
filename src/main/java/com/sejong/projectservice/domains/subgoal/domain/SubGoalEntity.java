package com.sejong.projectservice.domains.subgoal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JsonIgnore
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    public static SubGoalEntity from(SubGoalDto subGoalDto) {
        return SubGoalEntity.builder()
                .id(null)
                .content(subGoalDto.getContent())
                .completed(subGoalDto.getCompleted())
                .createdAt(subGoalDto.getCreatedAt())
                .updatedAt(subGoalDto.getUpdatedAt())
                .build();
    }

    public static SubGoalEntity of(String content, boolean isCompleted, LocalDateTime createdAt, LocalDateTime updatedAt, ProjectEntity projectEntity) {
        SubGoalEntity subGoalEntity = SubGoalEntity.builder()
                .id(null)
                .content(content)
                .completed(isCompleted)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        projectEntity.addSubGoal(subGoalEntity);
        return subGoalEntity;

    }

    public void assignProjectEntity(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    public void update(SubGoalDto subGoalDto) {
        this.id = subGoalDto.getId();
        this.content = subGoalDto.getContent();
        this.completed = subGoalDto.getCompleted();
        this.createdAt = subGoalDto.getCreatedAt();
        this.updatedAt = subGoalDto.getUpdatedAt();
    }

    public void check(){
        if(completed) completed = false;
        else completed = true;
    }
}
