package com.sejong.projectservice.domains.document.repository;

import com.sejong.projectservice.domains.document.domain.Document;

public interface DocumentRepository {
    Document findByIdAndProjectId(Long documentId, Long projectId);

    Document findById(Long documentId);

    Document save(Document document);

    void delete(Document document);
}
