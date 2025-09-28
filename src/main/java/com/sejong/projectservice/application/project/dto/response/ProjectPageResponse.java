package com.sejong.projectservice.application.project.dto.response;

import com.sejong.projectservice.core.project.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

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

    public static ProjectPageResponse from(Page<Project> projectPage, Map<String , String> usernames) {

        List<ProjectSimpleInfo> projectSimpleInfos = projectPage.stream()
                .map(project->{
                    return ProjectSimpleInfo.from(project, usernames);
                })
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
