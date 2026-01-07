package com.sejong.projectservice.domains.techstack.repository;

import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackRepository extends JpaRepository<TechStackEntity, Long> {
    Optional<TechStackEntity> findByName(String name);
}
