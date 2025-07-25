package com.sejong.projectservice.application.project.dto.response;

import com.sejong.projectservice.core.common.PageResult;
import com.sejong.projectservice.core.project.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectPageResponse {
    List<ProjectSimpleInfo> projects;
    int size;
    int element;
    Long totalElements;
    int page;

    public static ProjectPageResponse from(PageResult<Project> projectPage) {

        List<ProjectSimpleInfo> projectSimpleInfos = projectPage.getContent().stream()
                .map(ProjectSimpleInfo::from)
                .toList();

        return ProjectPageResponse.builder()
                .projects(projectSimpleInfos)
                .size(projectPage.getSize())
                .element(projectPage.getNumber())
                .page(projectPage.getTotalPages())
                .totalElements(projectPage.getTotalElements())
                .build();
    }
}
