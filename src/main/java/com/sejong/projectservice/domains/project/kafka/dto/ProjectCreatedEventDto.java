package com.sejong.projectservice.domains.project.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectCreatedEvent {
    private Long projectId;

    public static ProjectCreatedEvent of(Long projectId) {
        return new ProjectCreatedEvent(projectId);
    }
}
