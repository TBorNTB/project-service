package com.sejong.projectservice.core.subgoal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public void check(){
        if(completed) completed = false;
        else completed = true;
    }
}
