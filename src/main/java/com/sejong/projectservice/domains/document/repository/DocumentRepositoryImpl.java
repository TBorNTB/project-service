package com.sejong.projectservice.domains.document.repository;

import com.sejong.projectservice.domains.document.domain.Document;
import com.sejong.projectservice.domains.document.domain.DocumentEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final ProjectJpaRepository projectJpaRepository;
    private final DocumentJpaRepository documentJpaRepository;

    @Override
    public Document findByIdAndProjectId(Long documentId, Long projectId) {
        DocumentEntity documentEntity = documentJpaRepository.findByIdAndProjectId(documentId, projectId)
                .orElseThrow(() -> new RuntimeException("Document not found or not belongs in Project"));
        return documentEntity.toDomain();
    }

    @Override
    public Document findById(Long documentId) {
        DocumentEntity documentEntity = documentJpaRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return documentEntity.toDomain();
    }

    @Override
    public Document save(Document document) {
        DocumentEntity documentEntity;

        if (document.getId() == null) {
            ProjectEntity projectEntity = projectJpaRepository.findById(document.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            documentEntity = DocumentEntity.from(document, projectEntity);
            DocumentEntity savedDocumentEntity = documentJpaRepository.save(documentEntity);
            return savedDocumentEntity.toDomain();
        } else {
            documentEntity = documentJpaRepository.findById(document.getId())
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            documentEntity.update(document);
            return documentEntity.toDomain();
        }
    }

    @Override
    public void delete(Document document) {
        DocumentEntity documentEntity = documentJpaRepository.findById(document.getId())
                .orElseThrow(() -> new RuntimeException("Document not found"));
        ProjectEntity projectEntity = documentEntity.getProjectEntity();
        projectEntity.removeDocument(documentEntity);
        documentJpaRepository.delete(documentEntity);
    }
}
