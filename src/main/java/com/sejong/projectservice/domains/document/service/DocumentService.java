package com.sejong.projectservice.domains.document.service;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.document.dto.DocumentInfoRes;
import com.sejong.projectservice.domains.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.domains.document.kafka.dto.DocumentCreatedEventDto;
import com.sejong.projectservice.domains.document.kafka.dto.DocumentDeletedEventDto;
import com.sejong.projectservice.domains.document.kafka.dto.DocumentUpdatedEventDto;
import com.sejong.projectservice.domains.document.repository.DocumentRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import com.sejong.projectservice.support.common.internal.UserExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final UserExternalService userExternalService;
    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request, String username) {
        userExternalService.validateExistence(username);
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        DocumentEntity documentEntity = DocumentEntity.of(
                request.getTitle(),
                request.getDescription(),
                request.getThumbnailUrl(),
                request.getContent(),
                projectEntity
        );

        DocumentEntity savedDocumentEntity = documentRepository.save(documentEntity);
        applicationEventPublisher.publishEvent(DocumentCreatedEventDto.of(savedDocumentEntity.getId()));
        return DocumentInfoRes.from(savedDocumentEntity);
    }

    @Transactional(readOnly = true)
    public DocumentInfoRes getDocument(Long documentId) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));
        return DocumentInfoRes.from(documentEntity);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request, String username) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));
        ProjectEntity projectEntity = projectRepository.findById(documentEntity.getProjectEntity().getId())
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        documentEntity.update(request.getTitle(), request.getContent(), request.getDescription(),
                request.getThumbnailUrl());
        applicationEventPublisher.publishEvent(DocumentUpdatedEventDto.of(documentEntity.getId()));
        return DocumentInfoRes.from(documentEntity);
    }

    @Transactional
    public void deleteDocument(Long documentId, String username) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new BaseException(ExceptionType.DOCUMENT_NOT_FOUND));
        ProjectEntity projectEntity = projectRepository.findById(documentEntity.getProjectEntity().getId())
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        documentRepository.deleteById(documentEntity.getId());
        applicationEventPublisher.publishEvent(DocumentDeletedEventDto.of(documentEntity.getId()));
    }
}
