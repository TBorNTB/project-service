package com.sejong.projectservice.domains.project.kafka.listener;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.kafka.ProjectEventPublisher;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectCreatedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectDeletedEventDto;
import com.sejong.projectservice.domains.project.kafka.dto.ProjectUpdatedEventDto;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectKafkaEventListener {
    private final ProjectRepository projectRepository;
    private final ProjectEventPublisher projectEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(ProjectCreatedEventDto event){
        ProjectEntity project = projectRepository.findById(event.getProjectId())
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));

        projectEventPublisher.publishCreated(project);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(ProjectUpdatedEventDto event){
        ProjectEntity project = projectRepository.findById(event.getProjectId())
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));

        projectEventPublisher.publishUpdated(project);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(ProjectDeletedEventDto event) {
        projectEventPublisher.publishDeleted(event.getProjectId().toString());
    }

}
