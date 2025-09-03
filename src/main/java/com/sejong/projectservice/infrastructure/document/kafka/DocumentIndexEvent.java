package com.sejong.projectservice.infrastructure.document.kafka;


import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.infrastructure.project.kafka.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private DocumentEvent documentEvent;

    public static DocumentIndexEvent of(Document document, Type type, long occurredAt) {
        DocumentEvent documentEvent = DocumentEvent.from(document);
        return DocumentIndexEvent.builder()
                .aggregatedId(documentEvent.getId())
                .type(type)
                .occurredAt(occurredAt)
                .documentEvent(documentEvent)
                .build();
    }

    public static DocumentIndexEvent deleteOf(String documentId, Type type, long occurredAt) {
        return DocumentIndexEvent.builder()
                .aggregatedId(documentId)
                .type(type)
                .occurredAt(occurredAt)
                .build();
    }
}