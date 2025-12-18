package com.sejong.archiveservice.infrastructure.news.repository;

import com.sejong.archiveservice.infrastructure.news.entity.NewsEntity;
import feign.Param;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArchiveJpaRepository extends JpaRepository<NewsEntity, Long> {
    boolean existsById(Long archiveId);

    Page<NewsEntity> findAll(Pageable pageable);

    // ID 기준 DESC 커서 페이지네이션
    @Query("SELECT a FROM NewsEntity a WHERE a.id < :cursor ORDER BY a.id DESC")
    List<NewsEntity> findByCursorDesc(@Param("cursor") Long cursor, Pageable pageable);

    // ID 기준 ASC 커서 페이지네이션
    @Query("SELECT a FROM NewsEntity a WHERE a.id > :cursor ORDER BY a.id ASC")
    List<NewsEntity> findByCursorAsc(@Param("cursor") Long cursor, Pageable pageable);

    // 첫 페이지 조회 (커서 없음)
    @Query("SELECT a FROM NewsEntity a ORDER BY a.id DESC")
    List<NewsEntity> findFirstPageDesc(Pageable pageable);

    @Query("SELECT a FROM NewsEntity a ORDER BY a.id ASC")
    List<NewsEntity> findFirstPageAsc(Pageable pageable);

    @Query("SELECT count(ne) FROM NewsEntity ne")
    Long getNewsCount();
}
