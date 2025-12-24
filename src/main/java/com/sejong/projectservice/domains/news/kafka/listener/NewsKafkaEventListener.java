package com.sejong.projectservice.domains.news.kafka.listener;

import com.sejong.projectservice.domains.news.domain.NewsEntity;
import com.sejong.projectservice.domains.news.kafka.NewsEventPublisher;
import com.sejong.projectservice.domains.news.repository.ArchiveRepository;
import com.sejong.projectservice.domains.news.util.NewsMapper;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(ProjectCreatedEventDto event) {
        NewsEntity news = archiveRepository.findById(event.getProjectId())
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        newsEventPublisher.publishCreated(NewsMapper.toDomain(news));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(ProjectUpdatedEventDto event) {
        NewsEntity news = archiveRepository.findById(event.getProjectId())
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        newsEventPublisher.publishUpdated(NewsMapper.toDomain(news));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(ProjectDeletedEventDto event) {
        NewsEntity news = archiveRepository.findById(event.getProjectId())
                .orElseThrow(() -> new BaseException(ExceptionType.NEWS_NOT_FOUND));

        newsEventPublisher.publishDeleted(news.getId());
    }
}
