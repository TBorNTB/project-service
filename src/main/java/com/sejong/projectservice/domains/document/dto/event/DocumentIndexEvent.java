package com.sejong.projectservice.domains.document.dto.event;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
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
public class DocumentIndexEvent {
    private String aggregatedId;
    private Type type;
    private long occurredAt;
    private DocumentEvent documentEvent;

    public static DocumentIndexEvent of(DocumentEntity documentEntity, FileUploader fileUploader, Type type, long occurredAt) {
        DocumentEvent documentEvent = DocumentEvent.from(documentEntity, fileUploader);
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