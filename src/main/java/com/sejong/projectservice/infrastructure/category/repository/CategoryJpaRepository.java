package com.sejong.projectservice.infrastructure.category.repository;

import com.sejong.projectservice.infrastructure.category.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(String name);
}
