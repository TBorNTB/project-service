package com.sejong.projectservice.infrastructure.techstack.repository;

import com.sejong.projectservice.core.techstack.TechStackRepository;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechStackJpaRepository extends JpaRepository<TechStackEntity,Long> {
    Optional<TechStackEntity> findByName(String name);
}
