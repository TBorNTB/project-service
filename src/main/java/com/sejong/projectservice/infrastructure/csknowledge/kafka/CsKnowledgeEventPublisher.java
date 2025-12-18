package com.sejong.archiveservice.infrastructure.csknowledge.kafka;

import static com.sejong.archiveservice.infrastructure.common.Type.CREATED;
import static com.sejong.archiveservice.infrastructure.common.Type.UPDATED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.archiveservice.core.csknowledge.CsKnowledge;
import com.sejong.archiveservice.infrastructure.common.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CsKnowledgeEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String CS_KNOWLEDGE_EVENTS = "cs-knowledge";

    public void publishCreated(CsKnowledge csKnowledge){
        publish(csKnowledge, CREATED);
    }

    public void publishUpdated(CsKnowledge csKnowledge) {
        publish(csKnowledge, UPDATED);
    }

    public void publishDeleted(Long csKnowledgeId) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.deleteOf(csKnowledgeId.toString(), Type.DELETED, System.currentTimeMillis());
        kafkaTemplate.send(CS_KNOWLEDGE_EVENTS, csKnowledgeId.toString(),  toJsonString(event));
    }

    private void publish(CsKnowledge csKnowledge, Type type) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.of(csKnowledge, type, System.currentTimeMillis());
        kafkaTemplate.send(CS_KNOWLEDGE_EVENTS, csKnowledge.getId().toString(), toJsonString(event));
    }

    private String toJsonString(Object object) {
        try {
            String message = objectMapper.writeValueAsString(object);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 직렬화 실패: " + e.getMessage(), e);
        }
    }
}