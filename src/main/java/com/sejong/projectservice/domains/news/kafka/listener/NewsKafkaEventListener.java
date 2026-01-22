package com.sejong.projectservice.domains.news.kafka.listener;

import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.kafka.NewsEventPublisher;
import com.sejong.projectservice.domains.news.kafka.dto.NewsCreatedEventDto;
import com.sejong.projectservice.domains.news.kafka.dto.NewsDeletedEventDto;
import com.sejong.projectservice.domains.news.kafka.dto.NewsUpdatedEventDto;
import com.sejong.projectservice.domains.news.repository.ArchiveRepository;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectCreatedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectDeletedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectUpdatedEventDto;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NewsKafkaEventListener {

    private final ArchiveRepository archiveRepository;
    private final NewsEventPublisher newsEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCreated(NewsCreatedEventDto event) {
        NewsEntity newsEntity = archiveRepository.findById(event.getPostId())
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        newsEventPublisher.publishCreated(newsEntity);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onUpdated(NewsUpdatedEventDto event) {
        NewsEntity newsEntity = archiveRepository.findById(event.getPostId())
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        newsEventPublisher.publishUpdated(newsEntity);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDeleted(NewsDeletedEventDto event) {

        newsEventPublisher.publishDeleted(event.getPostId());
    }
}
