package com.sejong.projectservice.application.project.dto.request;

import com.sejong.projectservice.core.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectUpdateRequest {
    private String title;
    private String description;
    private ProjectStatus projectStatus;
    private String thumbnailUrl;
}
