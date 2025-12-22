package com.sejong.projectservice.domains.document.service;

import com.sejong.projectservice.domains.document.domain.Document;
import com.sejong.projectservice.domains.document.dto.DocumentCreateReq;
import com.sejong.projectservice.domains.document.dto.DocumentInfoRes;
import com.sejong.projectservice.domains.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.domains.document.repository.DocumentRepository;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;

import java.util.UUID;

import com.sejong.projectservice.domains.document.kafka.DocumentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentEventPublisher documentEventPublisher;
    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request, String username) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        Document document = Document.of(request, generateYorkieDocumentId(), projectEntity);

        Document savedDocument = documentRepository.save(document);
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
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request, String username) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = projectRepository.findById(document.getProjectEntity().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        document.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        documentEventPublisher.publishUpdated(document);
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public void deleteDocument(Long documentId, String username) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = projectRepository.findById(document.getProjectEntity().getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        documentRepository.deleteById(document.getId());
        documentEventPublisher.publishDeleted(documentId.toString());
    }
}
