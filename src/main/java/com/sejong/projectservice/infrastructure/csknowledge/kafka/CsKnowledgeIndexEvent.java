package com.sejong.projectservice.infrastructure.csknowledge.kafka;


import com.sejong.projectservice.core.csknowledge.CsKnowledge;
import com.sejong.projectservice.infrastructure.project.kafka.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeIndexEvent {
    private CsKnowledgeEvent csKnowledgeEvent;
    private Type type;
    private long occurredAt;

    public static CsKnowledgeIndexEvent of(CsKnowledge csKnowledge, Type type, long occurredAt) {
        return CsKnowledgeIndexEvent.builder()
                .csKnowledgeEvent(CsKnowledgeEvent.from(csKnowledge))
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }

    public static CsKnowledgeIndexEvent deleteOf(String csKnowledgeId, Type type, long occurredAt) {
        return CsKnowledgeIndexEvent.builder()
                .csKnowledgeEvent(null)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}