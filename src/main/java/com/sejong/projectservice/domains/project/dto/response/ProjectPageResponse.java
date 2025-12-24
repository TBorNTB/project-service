package com.sejong.projectservice.domains.project.dto.response;

import com.sejong.projectservice.support.common.internal.response.UserNameInfo;

import java.util.List;
import java.util.Map;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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

    public static ProjectPageResponse from(Page<ProjectEntity> projectPage, Map<String, UserNameInfo> userNameInfos) {

        List<ProjectSimpleInfo> projectSimpleInfos = projectPage.stream()
                .map(project -> {
                    return ProjectSimpleInfo.from(project, userNameInfos);
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
