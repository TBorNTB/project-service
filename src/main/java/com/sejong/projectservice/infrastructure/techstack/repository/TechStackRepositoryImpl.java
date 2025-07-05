package com.sejong.projectservice.infrastructure.techstack.repository;

import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.core.techstack.TechStackRepository;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TechStackRepositoryImpl implements TechStackRepository {
    private final TechStackJpaRepository techStackJpaRepository;

    @Override
    public TechStack findOrCreateByName(String name) {
        return techStackJpaRepository.findByName(name)
                .map(TechStackEntity::toDomain)
                .orElseGet(() -> {
                    TechStackEntity saved = techStackJpaRepository.save(new TechStackEntity(null, name));
                    return saved.toDomain();
                });
    }
}
