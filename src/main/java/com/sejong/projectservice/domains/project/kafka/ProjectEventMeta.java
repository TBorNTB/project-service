package com.sejong.projectservice.domains.project.kafka;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.support.common.constants.Type;
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

    public static ProjectEventMeta of(ProjectEntity project, Type type, long occurredAt) {
        ProjectEvent document = ProjectEvent.from(project);
        return ProjectEventMeta.builder()
                .aggregatedId(document.getId())
                .type(type)
                .occurredAt(occurredAt)
                .projectEvent(document)
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
