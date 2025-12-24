package com.sejong.projectservice.domains.csknowledge.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;

import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
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

    public void publishCreated(CsKnowledgeEntity csKnowledgeEntity){
        publish(csKnowledgeEntity, CREATED);
    }

    public void publishUpdated(CsKnowledgeEntity csKnowledgeEntity) {
        publish(csKnowledgeEntity, UPDATED);
    }

    public void publishDeleted(Long csKnowledgeId) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.deleteOf(csKnowledgeId.toString(), DELETED, System.currentTimeMillis());
        kafkaTemplate.send(CS_KNOWLEDGE_EVENTS, csKnowledgeId.toString(),  toJsonString(event));
    }

    private void publish(CsKnowledgeEntity csKnowledgeEntity, Type type) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.of(csKnowledgeEntity, type, System.currentTimeMillis());
        kafkaTemplate.send(CS_KNOWLEDGE_EVENTS, csKnowledgeEntity.getId().toString(), toJsonString(event));
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