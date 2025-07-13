package com.sejong.projectservice.infrastructure.yorkie.repository;

import com.sejong.projectservice.infrastructure.yorkie.YorkieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface YorkieJpaRepository extends JpaRepository<YorkieEntity, Long> {

    @Query("select y.yorkieId from YorkieEntity y where y.projectId = :projectId ")
    Optional<Long> findByProjectId(@Param("projectId")Long projectId);
}
