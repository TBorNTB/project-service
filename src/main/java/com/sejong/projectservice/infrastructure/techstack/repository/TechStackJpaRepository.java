package com.sejong.projectservice.infrastructure.techstack.repository;

import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackJpaRepository extends JpaRepository<TechStackEntity, Long> {
    Optional<TechStackEntity> findByName(String name);
}
