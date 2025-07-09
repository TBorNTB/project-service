package com.sejong.projectservice.core.collaborator;

import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Collaborator {
    private Long id;
    private String collaboratorName;

    public static Collaborator from(String collaboratorName) {
        return Collaborator.builder()
                .id(null)
                .collaboratorName(collaboratorName)
                .build();
    }
}
