package com.sejong.projectservice.infrastructure.subgoal;

import com.sejong.projectservice.application.common.error.code.ErrorCode;
import com.sejong.projectservice.application.common.error.exception.ApiException;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.subgoal.SubGoalRepository;
import com.sejong.projectservice.infrastructure.mapper.Mapper;
import com.sejong.projectservice.infrastructure.project.entity.ProjectEntity;
import com.sejong.projectservice.infrastructure.project.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubGoalRepositoryImpl implements SubGoalRepository {
    private final ProjectJpaRepository projectJpaRepository;
    private final SubGoalJpaRepository subGoalJpaRepository;


    @Override
    public SubGoal findOne(Long subGoalId) {
        SubGoalEntity subGoalEntity = subGoalJpaRepository.findById(subGoalId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 서브 목표는 존재하지 않습니다."));

        return subGoalEntity.toDomain();
    }

    @Override
    public SubGoal update(SubGoal subGoal) {
        SubGoalEntity subGoalEntity = subGoalJpaRepository.findById(subGoal.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 서브 목표는 존재하지 않습니다."));

        subGoalEntity.update(subGoal);
        return subGoalEntity.toDomain();
    }

    @Override
    public SubGoal save(Long projectId, SubGoal subGoal) {

        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 project는 존재하지 않습니다."));

        SubGoalEntity subGoalEntity = SubGoalEntity.from(subGoal);
        subGoalEntity.assignProjectEntity(projectEntity);
        SubGoalEntity savedSubGoalEntity = subGoalJpaRepository.save(subGoalEntity);
        return savedSubGoalEntity.toDomain();
    }

    @Override
    public void delete(Long subGoalId) {
        subGoalJpaRepository.deleteById(subGoalId);
    }

    @Override
    public List<SubGoal> getAll(Long projectId) {
        List<SubGoalEntity> subGoalEntities = subGoalJpaRepository.findAllByProjectId(projectId);
        return subGoalEntities.stream()
                .map(SubGoalEntity::toDomain)
                .toList();
    }
}
