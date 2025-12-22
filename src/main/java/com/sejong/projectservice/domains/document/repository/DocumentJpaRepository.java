package com.sejong.projectservice.domains.document.repository;

import com.sejong.projectservice.domains.document.domain.Document;
import feign.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentJpaRepository extends JpaRepository<Document, Long> {
    Optional<Document> findDocumentEntityById(Long id);

    @Query("SELECT d FROM Document d WHERE d.id = :documentId AND d.projectEntity.id = :projectId")
    Optional<Document> findByIdAndProjectId(@Param("documentId") Long documentId,
                                            @Param("projectId") Long projectId);
}
