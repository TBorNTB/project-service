package com.sejong.projectservice.application.subgoal.controller.dto;

import com.sejong.projectservice.core.subgoal.SubGoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubGoalResponse {

    private String content;
    private boolean isCompleted;
    private String message;

    public static SubGoalResponse from(SubGoal subGoal) {
        return SubGoalResponse.builder()
                .content(subGoal.getContent())
                .isCompleted(subGoal.getCompleted())
                .message("하위 목표 추가 성공")
                .build();
    }
}
