package com.sejong.projectservice.infrastructure.document.repository;

import com.sejong.projectservice.core.document.domain.Document;
import com.sejong.projectservice.core.document.repository.DocumentRepository;
import com.sejong.projectservice.infrastructure.document.entity.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {
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
        DocumentEntity documentEntity = documentJpaRepository.save(DocumentEntity.from(document));
        return documentEntity.toDomain();
    }
}
