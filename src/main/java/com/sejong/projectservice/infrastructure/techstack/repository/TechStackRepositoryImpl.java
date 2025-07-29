package com.sejong.projectservice.infrastructure.techstack.repository;

import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.core.techstack.TechstackRepository;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TechStackRepositoryImpl implements TechstackRepository {

    private final TechStackJpaRepository techStackJpaRepository;

    @Override
    public TechStack save(TechStack techStack) {
        TechStackEntity techStackEntity;

        if (techStack.getId() == null) {
            techStackEntity = TechStackEntity.from(techStack);
            TechStackEntity savedTechstackEntity = techStackJpaRepository.save(techStackEntity);
            return savedTechstackEntity.toDomain();
        } else {
            techStackEntity = techStackJpaRepository.findById(techStack.getId())
                    .orElseThrow(() -> new RuntimeException("TechStack not found"));
            techStackEntity.update(techStack);
            TechStackEntity savedTechstackEntity = techStackJpaRepository.save(techStackEntity);
            return savedTechstackEntity.toDomain();
        }
    }

    @Override
    public TechStack findById(Long techStackId) {
        TechStackEntity techStackEntity = techStackJpaRepository.findById(techStackId)
                .orElseThrow(() -> new RuntimeException("TechStack not found"));
        return techStackEntity.toDomain();
    }

    @Override
    public void deleteById(Long techStackId) {
        techStackJpaRepository.deleteById(techStackId);
    }
}
