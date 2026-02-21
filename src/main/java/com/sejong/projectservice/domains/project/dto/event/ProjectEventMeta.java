package com.sejong.projectservice.domains.project.dto.event;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.file.FileUploader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectEventMeta {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private ProjectEvent projectEvent;

    public static ProjectEventMeta of(ProjectEntity project, FileUploader fileUploader, Type type, long occurredAt) {
        ProjectEvent pe = ProjectEvent.from(project, fileUploader);
        return ProjectEventMeta.builder()
                .aggregatedId(pe.getId())
                .type(type)
                .occurredAt(occurredAt)
                .projectEvent(pe)
                .build();
    }

    public static ProjectEventMeta deleteOf(String projectId, Type type, long occurredAt) {
        return ProjectEventMeta.builder()
                .aggregatedId(projectId)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}
