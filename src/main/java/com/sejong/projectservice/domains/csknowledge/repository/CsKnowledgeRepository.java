package com.sejong.projectservice.domains.csknowledge.repository;


import com.sejong.projectservice.domains.csknowledge.domain.CsKnowledgeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CsKnowledgeRepository extends JpaRepository<CsKnowledgeEntity, Long> {

    List<CsKnowledgeEntity> findAllByCategoryEntity_Name(String name);

    List<CsKnowledgeEntity> findByIdGreaterThan(Long id, Pageable pageable);

    // ID 기준 DESC 커서 페이지네이션
    @Query("SELECT c FROM CsKnowledgeEntity c WHERE c.id < :cursor ORDER BY c.id DESC")
    List<CsKnowledgeEntity> findByCursorDesc(@Param("cursor") Long cursor, Pageable pageable);

    // ID 기준 ASC 커서 페이지네이션
    @Query("SELECT c FROM CsKnowledgeEntity c WHERE c.id > :cursor ORDER BY c.id ASC")
    List<CsKnowledgeEntity> findByCursorAsc(@Param("cursor") Long cursor, Pageable pageable);

    // 첫 페이지 조회 (커서 없음) - DESC
    @Query("SELECT c FROM CsKnowledgeEntity c ORDER BY c.id DESC")
    List<CsKnowledgeEntity> findFirstPageDesc(Pageable pageable);

    // 첫 페이지 조회 (커서 없음) - ASC
    @Query("SELECT c FROM CsKnowledgeEntity c ORDER BY c.id ASC")
    List<CsKnowledgeEntity> findFirstPageAsc(Pageable pageable);

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

    @Query("SELECT count(cse) FROM CsKnowledgeEntity cse " +
            "WHERE cse.createdAt >= :startDate AND cse.createdAt < :endDate")
    Long getCsCountByDate(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);
}
