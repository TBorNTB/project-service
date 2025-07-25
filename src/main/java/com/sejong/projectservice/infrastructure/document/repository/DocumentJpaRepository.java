package com.sejong.projectservice.infrastructure.document.repository;

import com.sejong.projectservice.infrastructure.document.entity.DocumentEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {
    Optional<DocumentEntity> findDocumentEntityById(Long documentId);
}
