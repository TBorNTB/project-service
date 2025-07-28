package com.sejong.projectservice.application.document.service;

import com.sejong.projectservice.application.project.assembler.Assembler;
import com.sejong.projectservice.application.project.dto.request.DocumentCreateReq;
import com.sejong.projectservice.application.project.dto.request.DocumentUpdateReq;
import com.sejong.projectservice.application.project.dto.response.DocumentInfoRes;
import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.repository.DocumentRepository;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public DocumentInfoRes createDocument(Long projectId, DocumentCreateReq request) {
        Project project = projectRepository.findOne(projectId);
        Document document = Assembler.toDocument(request, generateYorkieDocumentId());
        project.addDocument(document);
        Project savedProject = projectRepository.save(project);

        Document savedDocument = savedProject.getDocuments().stream()
                .filter(d -> d.getYorkieDocumentId().equals(document.getYorkieDocumentId()))
                .findFirst()
                .orElseThrow();
        return DocumentInfoRes.from(savedDocument);
    }

    private String generateYorkieDocumentId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 7);
    }

    public DocumentInfoRes getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId);
        return DocumentInfoRes.from(document);
    }

    @Transactional
    public DocumentInfoRes updateDocument(Long documentId, DocumentUpdateReq request) {
        Document document = documentRepository.findById(documentId);
        document.update(request.getTitle(), request.getContent(), request.getDescription(), request.getThumbnailUrl());
        Document savedDocument = documentRepository.save(document);
        return DocumentInfoRes.from(savedDocument);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId);
        documentRepository.delete(document);
    }
}
