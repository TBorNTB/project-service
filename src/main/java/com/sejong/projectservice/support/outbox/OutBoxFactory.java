package com.sejong.projectservice.support.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.dto.event.CsKnowledgeIndexEvent;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.dto.event.DocumentIndexEvent;
import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.dto.event.NewsIndexEvent;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.dto.event.ProjectEventMeta;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.file.FileUploader;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class OutBoxFactory {
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String topic;
    private String messageKey;
    private String payload;

    public static OutBoxFactory remove(ProjectEntity project, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.PROJECT;
        String projectId = project.getId().toString();
        ProjectEventMeta event = ProjectEventMeta.deleteOf(projectId, type, System.currentTimeMillis());
        return new OutBoxFactory(aggregateType.getAggregateType(), projectId, aggregateType.getEventType(type), aggregateType.getTopic(), projectId, toJsonString(event));
    }

    public static OutBoxFactory of(ProjectEntity project, FileUploader fileUploader, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.PROJECT;
        ProjectEventMeta event = ProjectEventMeta.of(project, fileUploader, type, System.currentTimeMillis());
        String aggregatedId = event.getAggregatedId();
        return new OutBoxFactory(aggregateType.getAggregateType(), aggregatedId, aggregateType.getEventType(type), aggregateType.getTopic(), aggregatedId, toJsonString(event));
    }

    public static OutBoxFactory remove(NewsEntity news, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.NEWS;
        String newsId = news.getId().toString();
        NewsIndexEvent event = NewsIndexEvent.deleteOf(newsId, type, System.currentTimeMillis());
        return new OutBoxFactory(aggregateType.getAggregateType(), newsId, aggregateType.getEventType(type), aggregateType.getTopic(), newsId, toJsonString(event));
    }

    public static OutBoxFactory of(NewsEntity news, FileUploader fileUploader, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.NEWS;
        String newsId = news.getId().toString();
        NewsIndexEvent event = NewsIndexEvent.of(news, fileUploader, type, System.currentTimeMillis());
        return new OutBoxFactory(aggregateType.getAggregateType(), newsId, aggregateType.getEventType(type), aggregateType.getTopic(), newsId, toJsonString(event));
    }

    public static OutBoxFactory remove(CsKnowledgeEntity csKnowledge, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.CS_KNOWLEDGE;
        String csKnowledgeId = csKnowledge.getId().toString();
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.deleteOf(csKnowledgeId, type, System.currentTimeMillis());
        return new OutBoxFactory(aggregateType.getAggregateType(), csKnowledgeId, aggregateType.getEventType(type), aggregateType.getTopic(), csKnowledgeId, toJsonString(event));
    }

    public static OutBoxFactory of(CsKnowledgeEntity csKnowledge, FileUploader fileUploader, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.CS_KNOWLEDGE;
        String csKnowledgeId = csKnowledge.getId().toString();
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.of(csKnowledge, fileUploader, type, System.currentTimeMillis());
        return new OutBoxFactory(aggregateType.getAggregateType(), csKnowledgeId, aggregateType.getEventType(type), aggregateType.getTopic(), csKnowledgeId, toJsonString(event));
    }

    public static OutBoxFactory remove(DocumentEntity document, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.DOCUMENT;
        String documentId = document.getId().toString();
        DocumentIndexEvent event = DocumentIndexEvent.deleteOf(documentId, Type.DELETED, System.currentTimeMillis());
        return new OutBoxFactory(aggregateType.getAggregateType(), documentId, aggregateType.getEventType(type), aggregateType.getTopic(), documentId, toJsonString(event));
    }

    public static OutBoxFactory of(DocumentEntity document, Type type) {
        OutboxAggregateType aggregateType = OutboxAggregateType.DOCUMENT;
        DocumentIndexEvent event = DocumentIndexEvent.of(document, type, System.currentTimeMillis());
        String aggregatedId = event.getAggregatedId();
        return new OutBoxFactory(aggregateType.getAggregateType(), aggregatedId, aggregateType.getEventType(type), aggregateType.getTopic(), aggregatedId, toJsonString(event));
    }

    private static String toJsonString(Object object) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String message = objectMapper.writeValueAsString(object);
            return message;
        } catch (JsonProcessingException e) {
            throw new BaseException(ExceptionType.JSON_PARSHING_ERROR);
        }
    }
}
