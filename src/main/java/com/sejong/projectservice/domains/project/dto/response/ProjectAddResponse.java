package com.sejong.projectservice.domains.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAddResponse {
    private String title;
    private String message;

    public static ProjectAddResponse from(String title, String message){
        return ProjectAddResponse.builder()
                .title(title)
                .message(message)
                .build();
    }
}
