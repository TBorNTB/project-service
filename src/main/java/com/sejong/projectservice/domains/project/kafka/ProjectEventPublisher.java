package com.sejong.projectservice.domains.project.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String PROJECT_EVENTS = "project";

    public void publishCreated(ProjectEntity project) {
        publish(project, Type.CREATED);
    }

    public void publishUpdated(ProjectEntity project) {
        publish(project, Type.UPDATED);
    }

    public void publishDeleted(String projectId) {
        ProjectIndexEvent event = ProjectIndexEvent.deleteOf(projectId, Type.DELETED, System.currentTimeMillis());
        kafkaTemplate.send(PROJECT_EVENTS, projectId, toJsonString(event));
    }

    private void publish(ProjectEntity project, Type type) {
        ProjectIndexEvent event = ProjectIndexEvent.of(project, type, System.currentTimeMillis());

        kafkaTemplate.send(PROJECT_EVENTS, event.getAggregatedId(), toJsonString(event));
        log.info("발행완료");
    }

    private String toJsonString(Object object) {
        try {
            String message = objectMapper.writeValueAsString(object);
            return message;
        } catch (JsonProcessingException e) {
            throw new BaseException(ExceptionType.JSON_PARSHING_ERROR);
        }
    }
}
