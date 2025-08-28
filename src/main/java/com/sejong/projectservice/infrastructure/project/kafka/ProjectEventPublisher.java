package com.sejong.projectservice.infrastructure.project.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.project.kafka.enums.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String PROJECT_EVENTS = "project-events";

    public void publishCreated(Project project){
        publish(project, Type.CREATED);
    }

    public void publishUpdated(Project project) {
        publish(project, Type.UPDATED);
    }

    public void publishDeleted(String projectId) {
        ProjectIndexEvent event = ProjectIndexEvent.deleteOf(projectId, Type.DELETED, System.currentTimeMillis());
        kafkaTemplate.send(PROJECT_EVENTS, projectId,  toJsonString(event));
    }

    private void publish(Project project, Type type) {
        ProjectIndexEvent event = ProjectIndexEvent.of(project, type, System.currentTimeMillis());
        kafkaTemplate.send(PROJECT_EVENTS, event.getAggregatedId(), toJsonString(event));
    }

    private String toJsonString(Object object) {
        try {
            String message = objectMapper.writeValueAsString(object);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 직렬화 실패");
        }
    }
}
