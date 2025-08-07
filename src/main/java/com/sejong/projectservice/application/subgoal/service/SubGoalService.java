package com.sejong.projectservice.application.subgoal.service;

import com.sejong.projectservice.application.subgoal.controller.dto.SubGoalCheckResponse;
import com.sejong.projectservice.application.subgoal.controller.dto.SubGoalDeleteResponse;
import com.sejong.projectservice.application.subgoal.controller.dto.SubGoalResponse;
import com.sejong.projectservice.core.project.domain.Project;
import com.sejong.projectservice.core.project.repository.ProjectRepository;
import com.sejong.projectservice.core.subgoal.SubGoal;
import com.sejong.projectservice.core.subgoal.SubGoalRepository;
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
    public SubGoalCheckResponse updateCheck(String userName, Long projectId , Long subGoalId) {
        Project project = projectRepository.findOne(projectId);
        project.ensureCollaboratorExists(userName);
        SubGoal subGoal = subGoalRepository.findOne(subGoalId);
        subGoal.check();
        SubGoal updatedSubGoal = subGoalRepository.update(subGoal);
        return SubGoalCheckResponse.from(updatedSubGoal);
    }

    @Transactional
    public SubGoalResponse create(String userName, Long projectId, String content) {
        Project project = projectRepository.findOne(projectId);
        project.ensureCollaboratorExists(userName);
        SubGoal subGoal = SubGoal.from(content, false, LocalDateTime.now(), LocalDateTime.now());
        SubGoal savedSubGoal = subGoalRepository.save(projectId, subGoal);
        return SubGoalResponse.from(savedSubGoal);
    }

    @Transactional
    public SubGoalDeleteResponse remove(String userName, Long projectId, Long subGoalId) {
        Project project = projectRepository.findOne(projectId);
        project.ensureCollaboratorExists(userName);
        subGoalRepository.delete(subGoalId);
        return SubGoalDeleteResponse.of(subGoalId);
    }

    public List<SubGoal> getAll(Long projectId) {
        List<SubGoal> subGoals = subGoalRepository.getAll(projectId);
        return subGoals;
    }
}
