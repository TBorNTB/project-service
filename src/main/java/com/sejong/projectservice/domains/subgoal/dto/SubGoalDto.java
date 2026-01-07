package com.sejong.projectservice.domains.subgoal.dto;

import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
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
public class SubGoalDto {

    private Long id;
    private String content;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SubGoalDto from(String content, Boolean completed, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return SubGoalDto.builder()
                .id(null)
                .content(content)
                .completed(completed)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static List<SubGoalDto> toDtoList(List<SubGoalEntity> subGoals) {
        return subGoals.stream()
                .map(SubGoalDto::toDto).toList();
    }

    private static SubGoalDto toDto(SubGoalEntity subGoalEntity) {
        return SubGoalDto.builder()
                .id(subGoalEntity.getId())
                .content(subGoalEntity.getContent())
                .completed(subGoalEntity.getCompleted())
                .createdAt(subGoalEntity.getCreatedAt())
                .updatedAt(subGoalEntity.getUpdatedAt())
                .build();
    }

}
