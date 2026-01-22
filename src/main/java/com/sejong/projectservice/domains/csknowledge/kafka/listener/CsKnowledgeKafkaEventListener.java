package com.sejong.projectservice.domains.csknowledge.kafka.listener;

import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import com.sejong.projectservice.domains.csknowledge.kafka.CsKnowledgeEventPublisher;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeCreatedEventDto;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeDeletedEventDto;
import com.sejong.projectservice.domains.csknowledge.kafka.dto.CsKnowledgeUpdatedEventDto;
import com.sejong.projectservice.domains.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CsKnowledgeKafkaEventListener {
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final CsKnowledgeEventPublisher csKnowledgeEventPublisher;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCreated(CsKnowledgeCreatedEventDto event){
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(event.getPostId())
                .orElseThrow(() -> new BaseException(ExceptionType.CS_KNOWLEDGE_NOT_FOUND));

        csKnowledgeEventPublisher.publishCreated(entity);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onUpdated(CsKnowledgeUpdatedEventDto event){
        CsKnowledgeEntity entity = csKnowledgeRepository.findById(event.getPostId())
                .orElseThrow(() -> new BaseException(ExceptionType.CS_KNOWLEDGE_NOT_FOUND));

        csKnowledgeEventPublisher.publishUpdated(entity);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDeleted(CsKnowledgeDeletedEventDto event) {
        csKnowledgeEventPublisher.publishDeleted(event.getPostId());
    }
}
