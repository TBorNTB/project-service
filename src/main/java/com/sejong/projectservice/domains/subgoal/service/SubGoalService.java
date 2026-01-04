package com.sejong.projectservice.domains.subgoal.service;

import com.sejong.projectservice.domains.project.domain.ProjectEntity;
import com.sejong.projectservice.domains.project.repository.ProjectRepository;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDto;
import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalCheckResponse;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDeleteResponse;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalResponse;
import com.sejong.projectservice.domains.subgoal.repository.SubGoalRepository;
import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubGoalService {
    private final SubGoalRepository subGoalRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public SubGoalCheckResponse updateCheck(String username, Long projectId , Long subGoalId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        SubGoalEntity subGoalEntity = subGoalRepository.findById(subGoalId)
                .orElseThrow(() -> new BaseException(ExceptionType.SUBGOAL_NOT_FOUND));
        projectEntity.checkSubGoal(subGoalEntity.getId());
        return SubGoalCheckResponse.from(subGoalEntity);
    }

    @Transactional
    public SubGoalResponse create(String username, Long projectId, String content) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        SubGoalEntity subGoalEntity = SubGoalEntity.of(content, false, LocalDateTime.now(), LocalDateTime.now(), projectEntity);

        SubGoalEntity savedSubGoal = subGoalRepository.save(subGoalEntity);
        return SubGoalResponse.from(savedSubGoal);
    }

    @Transactional
    public SubGoalDeleteResponse remove(String username, Long projectId, Long subGoalId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionType.PROJECT_NOT_FOUND));
        projectEntity.validateUserPermission(username);
        SubGoalEntity subGoalEntity = subGoalRepository.findById(subGoalId)
                .orElseThrow(() -> new BaseException(ExceptionType.SUBGOAL_NOT_FOUND));
        projectEntity.removeSubGoal(subGoalEntity);
        subGoalRepository.deleteById(subGoalId);
        return SubGoalDeleteResponse.of(subGoalId);
    }

    public List<SubGoalDto> getAll(Long projectId) {
        List<SubGoalEntity> subGoalEntities = subGoalRepository.findAllByProjectId(projectId);
        return SubGoalDto.toDtoList(subGoalEntities);
    }
}
