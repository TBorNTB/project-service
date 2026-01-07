package com.sejong.projectservice.domains.project.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUpdatedEventDto {
    private Long projectId;

    public static ProjectUpdatedEventDto of(Long projectId) {
        return new ProjectUpdatedEventDto(projectId);
    }
}
