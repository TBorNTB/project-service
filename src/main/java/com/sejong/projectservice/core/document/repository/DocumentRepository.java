package com.sejong.projectservice.core.document.repository;

import com.sejong.projectservice.core.document.domain.Document;

public interface DocumentRepository {
    Document findByIdAndProjectId(Long documentId, Long projectId);

    Document findById(Long documentId);

    Document save(Document document);

    void delete(Document document);
}
