package com.sejong.projectservice.support.outbox;

import com.sejong.projectservice.support.common.constants.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxAggregateType {

    PROJECT("project", "project", "Project", "ProjectDeleted"),
    NEWS("news", "news", "News", "NewsDeleted"),
    CS_KNOWLEDGE("cs-knowledge", "cs-knowledge", "CsKnowledge", "CsKnowledgeDeleted"),
    DOCUMENT("document", "document", "Document", "DocumentDeleted");

    private final String aggregateType;
    private final String topic;
    private final String eventTypePrefix;
    private final String deletedEventType;

    public String getEventType(Type type) {
        if (type == Type.DELETED) {
            return deletedEventType;
        }
        return eventTypePrefix + type.name();
    }
}
