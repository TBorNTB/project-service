package com.sejong.projectservice.application.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUpdateResponse {
    private String title;
    private String message;

    public static ProjectUpdateResponse from(String title, String message){
        return ProjectUpdateResponse.builder()
                .title(title)
                .message(message)
                .build();
    }
}
