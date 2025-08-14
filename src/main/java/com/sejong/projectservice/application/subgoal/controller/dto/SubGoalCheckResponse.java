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
public class SubGoalCheckResponse {
    private Boolean isCheck;
    private String content;
    private String message;

    public static SubGoalCheckResponse from(SubGoal subGoal) {
        return SubGoalCheckResponse.builder()
                .isCheck(subGoal.getCompleted())
                .message("체크 선택 or 미선택 완료")
                .content(subGoal.getContent())
                .build();
    }
}
