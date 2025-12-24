package com.sejong.projectservice.domains.subgoal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubGoalDeleteResponse {
    private Long id;
    private String message;

    public static SubGoalDeleteResponse of(Long id) {
        return SubGoalDeleteResponse.builder()
                .id(id)
                .message("삭제 되었습니다.")
                .build();
    }
}
