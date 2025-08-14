package com.sejong.projectservice.core.project.domain;

import com.sejong.projectservice.core.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDocument {
    private String id;

    private String title;
    private String description;

    private ProjectStatus projectStatus;

    private String createdAt;

    private String thumbnailUrl;

    private List<String> projectCategories = new ArrayList<>();

    private List<String> projectTechStacks = new ArrayList<>();

    private List<String> collaborators = new ArrayList<>();

}
