package com.sejong.projectservice.domains.csknowledge.kafka.listener;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.kafka.CsKnowledgeEventPublisher;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeCreatedEventDto;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeDeletedEventDto;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeUpdatedEventDto;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CsKnowledgeKafkaEventListener {
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final CsKnowledgeEventPublisher csKnowledgeEventPublisher;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(CsKnowledgeCreatedEventDto event){
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(event.getPostId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        csKnowledgeEventPublisher.publishCreated(entity.toDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(CsKnowledgeUpdatedEventDto event){
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(event.getPostId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        csKnowledgeEventPublisher.publishUpdated(entity.toDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(CsKnowledgeDeletedEventDto event) {
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(event.getPostId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        csKnowledgeEventPublisher.publishDeleted(entity.getId());
    }
}
