package com.sejong.projectservice.domains.project.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDeletedEventDto {
    private Long projectId;

    public static ProjectDeletedEventDto of(Long projectId) {
        return new ProjectDeletedEventDto(projectId);
    }
}
