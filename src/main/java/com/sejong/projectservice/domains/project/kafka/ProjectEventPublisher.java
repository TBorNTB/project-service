package com.sejong.projectservice.domains.project.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.project.domain.ProjectDto;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.kafka.enums.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String PROJECT_EVENTS = "project";

    public void publishCreated(ProjectEntity project){
        publish2(project, Type.CREATED);
    }

    public void publishUpdated(ProjectEntity project) {
        publish2(project, Type.UPDATED);
    }

    public void publishDeleted(String projectId) {
        ProjectIndexEvent event = ProjectIndexEvent.deleteOf(projectId, Type.DELETED, System.currentTimeMillis());
        kafkaTemplate.send(PROJECT_EVENTS, projectId,  toJsonString(event));
    }

    private void publish(ProjectDto projectDto, Type type) {
        ProjectIndexEvent event = ProjectIndexEvent.of(projectDto, type, System.currentTimeMillis());
        kafkaTemplate.send(PROJECT_EVENTS, event.getAggregatedId(), toJsonString(event));
    }
    private void publish2(ProjectEntity project, Type type) {
        ProjectIndexEvent event = ProjectIndexEvent.of2(project, type, System.currentTimeMillis());
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
