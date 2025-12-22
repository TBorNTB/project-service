package com.sejong.projectservice.domains.subgoal.repository;

import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubGoalRepositoryImpl implements SubGoalRepository {
    private final ProjectJpaRepository projectJpaRepository;
    private final SubGoalJpaRepository subGoalJpaRepository;


    @Override
    public SubGoalDto findOne(Long subGoalId) {
        SubGoalEntity subGoalEntity = subGoalJpaRepository.findById(subGoalId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 서브 목표는 존재하지 않습니다."));

        return subGoalEntity.toDomain();
    }

    @Override
    public SubGoalDto update(SubGoalDto subGoalDto) {
        SubGoalEntity subGoalEntity = subGoalJpaRepository.findById(subGoalDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 서브 목표는 존재하지 않습니다."));

        subGoalEntity.update(subGoalDto);
        return subGoalEntity.toDomain();
    }

    @Override
    public SubGoalDto save(Long projectId, SubGoalDto subGoalDto) {

        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 project는 존재하지 않습니다."));

        SubGoalEntity subGoalEntity = SubGoalEntity.from(subGoalDto);
        subGoalEntity.assignProjectEntity(projectEntity);
        SubGoalEntity savedSubGoalEntity = subGoalJpaRepository.save(subGoalEntity);
        return savedSubGoalEntity.toDomain();
    }

    @Override
    public void delete(Long subGoalId) {
        subGoalJpaRepository.deleteById(subGoalId);
    }

    @Override
    public List<SubGoalDto> getAll(Long projectId) {
        List<SubGoalEntity> subGoalEntities = subGoalJpaRepository.findAllByProjectId(projectId);
        return subGoalEntities.stream()
                .map(SubGoalEntity::toDomain)
                .toList();
    }
}
