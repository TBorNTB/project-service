package com.sejong.projectservice.domains.subgoal.service;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectJpaRepository;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalCheckResponse;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDeleteResponse;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalResponse;
import com.sejong.projectservice.domains.subgoal.repository.SubGoalJpaRepository;
import com.sejong.projectservice.support.common.error.code.ErrorCode;
import com.sejong.projectservice.support.common.error.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubGoalService {
    private final SubGoalJpaRepository subGoalJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;

    @Transactional
    public SubGoalCheckResponse updateCheck(String username, Long projectId , Long subGoalId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        SubGoalEntity subGoalEntity = subGoalJpaRepository.findById(subGoalId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 서브 목표는 존재하지 않습니다."));
        subGoalEntity.check(); //변경감지 되어서 자동 update
        return SubGoalCheckResponse.from(subGoalEntity);
    }

    @Transactional
    public SubGoalResponse create(String username, Long projectId, String content) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        SubGoalEntity subGoalEntity = SubGoalEntity.of(content, false, LocalDateTime.now(), LocalDateTime.now(), projectEntity);

        SubGoalEntity savedSubGoal = subGoalJpaRepository.save(subGoalEntity);
        return SubGoalResponse.from(savedSubGoal);
    }

    @Transactional
    public SubGoalDeleteResponse remove(String username, Long projectId, Long subGoalId) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectEntity.validateUserPermission(username);
        subGoalJpaRepository.deleteById(subGoalId);
        return SubGoalDeleteResponse.of(subGoalId);
    }

    public List<SubGoalDto> getAll(Long projectId) {
        List<SubGoalEntity> subGoalEntities = subGoalJpaRepository.findAllByProjectId(projectId);
        return SubGoalDto.toDtoList(subGoalEntities);
    }
}
