package com.sejong.projectservice.domains.project.kafka;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectKafkaEventListener {
    private final ProjectRepository projectRepository;
    private final ProjectEventPublisher projectEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(ProjectCreatedEvent event){
        log.info("[AFTER_COMMIT] projectId={}", event.getProjectId());
        ProjectEntity project = projectRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectEventPublisher.publishCreated(project);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdated(ProjectUpdatedEvent event){
        ProjectEntity project = projectRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectEventPublisher.publishUpdated(project);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleted(ProjectDeletedEvent event) {
        ProjectEntity project = projectRepository.findById(event.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectEventPublisher.publishDeleted(project.getId().toString());
    }

}
