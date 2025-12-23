package com.sejong.projectservice.domains.project.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdatedEvent {
    private Long projectId;

    public static ProjectUpdatedEvent of(Long projectId) {
        return new ProjectUpdatedEvent(projectId);
    }
}
