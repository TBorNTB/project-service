package com.sejong.projectservice.domains.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAddResponse {
    private String title;
    private String message;
    private String content;
    private LocalDateTime endedAt;

    public static ProjectAddResponse from(String title, String message, String content, LocalDateTime endedAt){
        return ProjectAddResponse.builder()
                .title(title)
                .message(message)
                .content(content)
                .endedAt(endedAt)
                .build();
    }
}
