package com.sejong.projectservice.domains.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUpdateResponse {
    private Long id;
    private String title;
    private String message;

    public static ProjectUpdateResponse from(Long id, String title, String message){
        return ProjectUpdateResponse.builder()
                .id(id)
                .title(title)
                .message(message)
                .build();
    }
}
