package com.sejong.projectservice.domains.document.service;

import com.sejong.projectservice.domains.document.domain.Document;
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
        Document document = Document.of(request, generateYorkieDocumentId(), projectEntity);

        Document savedDocument = documentJpaRepository.save(document);
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
        Document document = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request, String username) {
        Document document = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = projectJpaRepository.findById(document.getProjectEntity().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        document.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        documentEventPublisher.publishUpdated(document);
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public void deleteDocument(Long documentId, String username) {
        Document document = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = projectJpaRepository.findById(document.getProjectEntity().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        documentJpaRepository.deleteById(document.getId());
        documentEventPublisher.publishDeleted(documentId.toString());
    }
}
