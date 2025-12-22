package com.sejong.projectservice.domains.document.service;

import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.document.dto.DocumentInfoRes;
import com.sejong.projectservice.domains.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.domains.document.repository.DocumentJpaRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectJpaRepository;

import java.util.UUID;

import com.sejong.projectservice.domains.document.kafka.DocumentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentEventPublisher documentEventPublisher;
    private final DocumentJpaRepository documentJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request, String username) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        DocumentEntity documentEntity = DocumentEntity.of(request, generateYorkieDocumentId(), projectEntity);

        DocumentEntity savedDocument = documentJpaRepository.save(documentEntity);
        documentEventPublisher.publishCreated(savedDocument);
        return DocumentInfoRes.from(savedDocument);
    }

    private String generateYorkieDocumentId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 7);
    }

    @Transactional(readOnly = true)
    public DocumentInfoRes getDocument(Long documentId) {
        DocumentEntity documentEntity = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return DocumentInfoRes.from(documentEntity);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request, String username) {
        DocumentEntity documentEntity = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = projectJpaRepository.findById(documentEntity.getProjectEntity().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        documentEntity.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        documentEventPublisher.publishUpdated(documentEntity);
        return DocumentInfoRes.from(documentEntity);
    }

    @Transactional
    public void deleteDocument(Long documentId, String username) {
        DocumentEntity documentEntity = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = projectJpaRepository.findById(documentEntity.getProjectEntity().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        documentJpaRepository.deleteById(documentEntity.getId());
        documentEventPublisher.publishDeleted(documentId.toString());
    }
}
