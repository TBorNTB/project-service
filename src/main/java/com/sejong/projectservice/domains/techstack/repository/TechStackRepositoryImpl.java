package com.sejong.projectservice.domains.techstack.repository;

import com.sejong.projectservice.domains.techstack.domain.TechStackDto;
import com.sejong.projectservice.domains.techstack.domain.TechStackEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TechStackRepositoryImpl implements TechstackRepository {

    private final TechStackJpaRepository techStackJpaRepository;

    @Override
    public TechStackDto save(TechStackDto techStackDto) {
        TechStackEntity techStackEntity;

        if (techStackDto.getId() == null) {
            techStackEntity = TechStackEntity.from(techStackDto);
            TechStackEntity savedTechstackEntity = techStackJpaRepository.save(techStackEntity);
            return savedTechstackEntity.toDomain();
        } else {
            techStackEntity = techStackJpaRepository.findById(techStackDto.getId())
                    .orElseThrow(() -> new RuntimeException("TechStack not found"));
            techStackEntity.update(techStackDto);
            TechStackEntity savedTechstackEntity = techStackJpaRepository.save(techStackEntity);
            return savedTechstackEntity.toDomain();
        }
    }

    @Override
    public TechStackDto findById(Long techStackId) {
        TechStackEntity techStackEntity = techStackJpaRepository.findById(techStackId)
                .orElseThrow(() -> new RuntimeException("TechStack not found"));
        return techStackEntity.toDomain();
    }

    @Override
    public void deleteById(Long techStackId) {
        techStackJpaRepository.deleteById(techStackId);
    }
}
