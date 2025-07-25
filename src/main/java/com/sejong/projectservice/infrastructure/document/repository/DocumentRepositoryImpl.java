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
    public Document findByDocumentId(Long documentId) {
        DocumentEntity documentEntity = documentJpaRepository.findDocumentEntityById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return documentEntity.toDomain();
    }
}
