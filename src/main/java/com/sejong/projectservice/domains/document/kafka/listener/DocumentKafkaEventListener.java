package com.sejong.projectservice.domains.document.kafka.listener;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.kafka.DocumentEventPublisher;
import com.sejong.projectservice.domains.document.kafka.dto.DocumentCreatedEventDto;
import com.sejong.projectservice.domains.document.kafka.dto.DocumentDeletedEventDto;
import com.sejong.projectservice.domains.document.kafka.dto.DocumentUpdatedEventDto;
import com.sejong.projectservice.domains.document.repository.DocumentRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DocumentKafkaEventListener {
    private final DocumentRepository documentRepository;
    private final DocumentEventPublisher documentEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCreated(DocumentCreatedEventDto event) {
        DocumentEntity entity = documentRepository.findById(event.getPostId())
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));

        documentEventPublisher.publishCreated(entity);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onUpdated(DocumentUpdatedEventDto event) {
        DocumentEntity entity = documentRepository.findById(event.getPostId())
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));

        documentEventPublisher.publishUpdated(entity);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDeleted(DocumentDeletedEventDto event) {
        documentEventPublisher.publishDeleted(event.getPostId().toString());
    }
}
