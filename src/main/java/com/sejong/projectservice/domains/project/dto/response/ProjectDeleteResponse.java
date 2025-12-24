package com.sejong.projectservice.domains.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDeleteResponse {
    private String title;
    private String message;

    public static ProjectDeleteResponse of(String title, String message) {
        return ProjectDeleteResponse.builder()
                .title(title)
                .message(message)
                .build();
    }
}
