package com.sejong.archiveservice.infrastructure.csknowledge;

import com.sejong.archiveservice.core.csknowledge.TechCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CsKnowledgeJpaRepository extends JpaRepository<CsKnowledgeEntity, Long> {

    List<CsKnowledgeEntity> findAllByTechCategory(TechCategory techCategory);

    List<CsKnowledgeEntity> findByIdGreaterThan(Long id, Pageable pageable);

    @Query(value = """
    SELECT * FROM cs_knowledge k
    WHERE k.category_name = :categoryName
      AND k.id NOT IN (
        SELECT s.cs_knowledge_id
        FROM sent_log s
        WHERE s.email = :email AND s.cs_knowledge_id IS NOT NULL
      )
    ORDER BY RAND()
    LIMIT 1
""", nativeQuery = true)
    Optional<CsKnowledgeEntity> findRandomUnsent(@Param("categoryName") String categoryName,
                                                 @Param("email") String email);

    @Query("SELECT count(cse) FROM CsKnowledgeEntity cse")
    Long getCsCount();
}
