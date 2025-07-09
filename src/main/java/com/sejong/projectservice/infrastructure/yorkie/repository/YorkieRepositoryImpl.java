package com.sejong.projectservice.infrastructure.yorkie.repository;

import com.sejong.projectservice.application.error.code.YorkieErrorCode;
import com.sejong.projectservice.application.error.exception.ApiException;
import com.sejong.projectservice.core.yorkie.Yorkie;

import com.sejong.projectservice.core.yorkie.YorkieRepository;
import com.sejong.projectservice.infrastructure.yorkie.YorkieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class YorkieRepositoryImpl implements YorkieRepository {
    private final YorkieJpaRepository yorkieJpaRepository;

    @Override
    @Transactional
    public Yorkie save(Yorkie yorkie) {
        YorkieEntity entity = YorkieEntity.from(yorkie);
        YorkieEntity savedEntity = yorkieJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Long findByProjectId(Long projectId) {
        Long yorkieId = yorkieJpaRepository.findByProjectId(projectId)
                .orElseThrow(()->new ApiException(YorkieErrorCode.NOT_FOUND_YORKIE_ID,"프로젝트_아이디 : "+projectId));
        return yorkieId;
    }
}
