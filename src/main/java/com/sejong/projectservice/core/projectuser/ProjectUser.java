package com.sejong.projectservice.core.projectuser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectUser {
    private Long id;
    private String collaboratorName;

    public static ProjectUser from(String collaboratorName) {
        return ProjectUser.builder()
                .id(null)
                .collaboratorName(collaboratorName)
                .build();
    }
}
