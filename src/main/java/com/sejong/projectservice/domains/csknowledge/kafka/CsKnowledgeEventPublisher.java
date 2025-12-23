package com.sejong.projectservice.domains.csknowledge.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeDto;

import com.sejong.projectservice.support.common.constants.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.sejong.projectservice.support.common.constants.Type.*;


@Service
@RequiredArgsConstructor
public class CsKnowledgeEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String CS_KNOWLEDGE_EVENTS = "cs-knowledge";

    public void publishCreated(CsKnowledgeDto csKnowledgeDto){
        publish(csKnowledgeDto, CREATED);
    }

    public void publishUpdated(CsKnowledgeDto csKnowledgeDto) {
        publish(csKnowledgeDto, UPDATED);
    }

    public void publishDeleted(Long csKnowledgeId) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.deleteOf(csKnowledgeId.toString(), DELETED, System.currentTimeMillis());
        kafkaTemplate.send(CS_KNOWLEDGE_EVENTS, csKnowledgeId.toString(),  toJsonString(event));
    }

    private void publish(CsKnowledgeDto csKnowledgeDto, Type type) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.of(csKnowledgeDto, type, System.currentTimeMillis());
        kafkaTemplate.send(CS_KNOWLEDGE_EVENTS, csKnowledgeDto.getId().toString(), toJsonString(event));
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