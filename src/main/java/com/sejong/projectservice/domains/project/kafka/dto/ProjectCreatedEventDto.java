package com.sejong.projectservice.domains.project.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectCreatedEventDto {
    private Long projectId;

    public static ProjectCreatedEventDto of(Long projectId) {
        return new ProjectCreatedEventDto(projectId);
    }
}
