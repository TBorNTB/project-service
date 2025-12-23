package com.sejong.projectservice.domains.project.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDeletedEvent {
    private Long projectId;

    public static ProjectDeletedEvent of(Long projectId) {
        return new ProjectDeletedEvent(projectId);
    }
}
