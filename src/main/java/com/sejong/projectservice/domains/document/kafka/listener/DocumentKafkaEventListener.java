package com.sejong.projectservice.domains.document.kafka.listener;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.kafka.DocumentEventPublisher;
import com.sejong.projectservice.domains.document.repository.DocumentRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectCreatedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectDeletedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectUpdatedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DocumentKafkaEventListener {
    private final DocumentRepository documentRepository;
    private final DocumentEventPublisher documentEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(ProjectCreatedEventDto event) {
        DocumentEntity entity = documentRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        documentEventPublisher.publishCreated(entity);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(ProjectUpdatedEventDto event) {
        DocumentEntity entity = documentRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        documentEventPublisher.publishUpdated(entity);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(ProjectDeletedEventDto event) {
        DocumentEntity entity = documentRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        documentEventPublisher.publishDeleted(entity.getId().toString());
    }
}
