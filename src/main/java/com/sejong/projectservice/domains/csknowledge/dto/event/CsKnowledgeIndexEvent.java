package com.sejong.projectservice.domains.csknowledge.dto.event;


import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.support.common.constants.Type;
import com.sejong.projectservice.support.common.file.FileUploader;
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

    public static CsKnowledgeIndexEvent of(CsKnowledgeEntity csKnowledgeEntity, FileUploader fileUploader, Type type, long occurredAt) {
        return CsKnowledgeIndexEvent.builder()
                .aggregatedId(csKnowledgeEntity.getId().toString())
                .csKnowledgeEvent(CsKnowledgeEvent.from(csKnowledgeEntity, fileUploader))
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