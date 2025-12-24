package com.sejong.projectservice.domains.csknowledge.kafka;


import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeDto;
import com.sejong.projectservice.support.common.constants.Type;
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

    public static CsKnowledgeIndexEvent of(CsKnowledgeEntity csKnowledgeEntity, Type type, long occurredAt) {
        return CsKnowledgeIndexEvent.builder()
                .csKnowledgeEvent(CsKnowledgeEvent.from(csKnowledgeEntity))
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