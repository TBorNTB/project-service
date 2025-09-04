package com.sejong.projectservice.infrastructure.project.kafka;

import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.project.kafka.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private ProjectEvent projectEvent;

    public static ProjectIndexEvent of(Project project,Type type, long occurredAt) {
        ProjectEvent document = ProjectEvent.from(project);
        return ProjectIndexEvent.builder()
                .aggregatedId(document.getId())
                .type(type)
                .occurredAt(occurredAt)
                .projectEvent(document)
                .build();
    }

    public static ProjectIndexEvent deleteOf(String projectId,Type type, long occurredAt) {
        return ProjectIndexEvent.builder()
                .aggregatedId(projectId)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}
