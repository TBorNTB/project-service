package com.sejong.projectservice.domains.project.dto.request;

import com.sejong.projectservice.domains.enums.ProjectStatus;
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
