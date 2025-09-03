package com.sejong.projectservice.infrastructure.document.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.infrastructure.project.kafka.ProjectIndexEvent;
import com.sejong.projectservice.infrastructure.project.kafka.enums.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String DOCUMENT_EVENTS = "aws.document.cdc.events.0";

    public void publishCreated(Document document){
        publish(document, Type.CREATED);
    }

    public void publishUpdated(Document document) {
        publish(document, Type.UPDATED);
    }

    public void publishDeleted(String documentId) {
        DocumentIndexEvent event = DocumentIndexEvent.deleteOf(documentId, Type.DELETED, System.currentTimeMillis());
        kafkaTemplate.send(DOCUMENT_EVENTS, documentId,  toJsonString(event));
    }

    private void publish(Document document, Type type) {
        DocumentIndexEvent event = DocumentIndexEvent.of(document, type, System.currentTimeMillis());
        kafkaTemplate.send(DOCUMENT_EVENTS, event.getAggregatedId(), toJsonString(event));
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
