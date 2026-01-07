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
    private String aggregatedId;
    private CsKnowledgeEvent csKnowledgeEvent;
    private Type type;
    private long occurredAt;

    public static CsKnowledgeIndexEvent of(CsKnowledgeEntity csKnowledgeEntity, Type type, long occurredAt) {
        return CsKnowledgeIndexEvent.builder()
                .aggregatedId(csKnowledgeEntity.getId().toString()) // 추후 outbox패턴 도입시 필요할 수 있어 이대로 유지 elastic 서비스는 동기화 했습니다. 필드명
                .csKnowledgeEvent(CsKnowledgeEvent.from(csKnowledgeEntity))
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }

    public static CsKnowledgeIndexEvent deleteOf(String csKnowledgeId, Type type, long occurredAt) {
        return CsKnowledgeIndexEvent.builder()
                .aggregatedId(csKnowledgeId)
                .csKnowledgeEvent(null)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}