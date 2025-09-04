package com.sejong.projectservice.application.document.service;

import com.sejong.projectservice.application.document.dto.DocumentCreateReq;
import com.sejong.projectservice.application.document.dto.DocumentInfoRes;
import com.sejong.projectservice.application.document.dto.DocumentUpdateReq;
import com.sejong.projectservice.application.project.assembler.Assembler;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.repository.DocumentRepository;

import java.util.UUID;

import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.infrastructure.document.kafka.DocumentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentEventPublisher documentEventPublisher;
    private final ProjectRepository projectRepository;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request, String username) {
        Project project = projectRepository.findOne(projectId);
        project.validateUserPermission(username);
        Document document = Assembler.toDocument(request, generateYorkieDocumentId(), projectId);
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
        Document document = documentRepository.findById(documentId);
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request, String username) {
        Document document = documentRepository.findById(documentId);
        Project project = projectRepository.findOne(document.getProjectId());
        project.validateUserPermission(username);
        document.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        Document savedDocument = documentRepository.save(document);
        documentEventPublisher.publishUpdated(savedDocument);
        return DocumentInfoRes.from(savedDocument);
    }

    @Transactional
    public void deleteDocument(Long documentId, String username) {
        Document document = documentRepository.findById(documentId);
        Project project = projectRepository.findOne(document.getProjectId());
        project.validateUserPermission(username);
        documentRepository.delete(document);
        documentEventPublisher.publishDeleted(documentId.toString());
    }
}
