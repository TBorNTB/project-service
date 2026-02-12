package com.sejong.projectservice.domains.csknowledge.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;

import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sejong.projectservice.support.common.constants.Type.*;


@Service
@RequiredArgsConstructor
public class CsKnowledgeEventPublisher {
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final FileUploader fileUploader;
    private final String CS_KNOWLEDGE_EVENTS = "cs-knowledge";

    public void publishCreated(CsKnowledgeEntity csKnowledgeEntity){
        publish(csKnowledgeEntity, CREATED);
    }

    public void publishUpdated(CsKnowledgeEntity csKnowledgeEntity) {
        publish(csKnowledgeEntity, UPDATED);
    }

    public void publishDeleted(Long csKnowledgeId) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.deleteOf(csKnowledgeId.toString(), DELETED, System.currentTimeMillis());
        outboxService.enqueue("cs-knowledge", csKnowledgeId.toString(), "CsKnowledgeDeleted", CS_KNOWLEDGE_EVENTS, csKnowledgeId.toString(), toJsonString(event));
    }

    private void publish(CsKnowledgeEntity csKnowledgeEntity, Type type) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.of(csKnowledgeEntity, fileUploader, type, System.currentTimeMillis());
        outboxService.enqueue("cs-knowledge", csKnowledgeEntity.getId().toString(), "CsKnowledge" + type.name(), CS_KNOWLEDGE_EVENTS, csKnowledgeEntity.getId().toString(), toJsonString(event));
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