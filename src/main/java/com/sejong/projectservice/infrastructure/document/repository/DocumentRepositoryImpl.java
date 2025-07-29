package com.sejong.projectservice.infrastructure.document.repository;

import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.repository.DocumentRepository;
import com.sejong.projectservice.infrastructure.document.entity.DocumentEntity;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.project.repository.ProjectJpaRepository;
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
        DocumentEntity documentEntity = DocumentEntity.from(document);
        ProjectEntity projectEntity = documentEntity.getProjectEntity();
        projectEntity.removeDocument(documentEntity);
        documentJpaRepository.delete(documentEntity);
    }
}
