package com.sejong.projectservice.domains.subgoal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubGoal {

    private Long id;
    private String content;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubGoal from(String content, Boolean completed,LocalDateTime createdAt, LocalDateTime updatedAt) {
        return SubGoal.builder()
                .id(null)
                .content(content)
                .completed(completed)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static List<SubGoal> toDtoList(List<SubGoalEntity> subGoals) {
        return subGoals.stream()
                .map(SubGoal::toDto).toList();
    }

    private static SubGoal toDto(SubGoalEntity subGoalEntity) {
        return SubGoal.builder()
                .id(subGoalEntity.getId())
                .content(subGoalEntity.getContent())
                .completed(subGoalEntity.getCompleted())
                .createdAt(subGoalEntity.getCreatedAt())
                .updatedAt(subGoalEntity.getUpdatedAt())
                .build();
    }

    public void check(){
        if(completed) completed = false;
        else completed = true;
    }
}
