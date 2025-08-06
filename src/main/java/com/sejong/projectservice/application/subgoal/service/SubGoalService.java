package com.sejong.projectservice.application.subgoal.service;

import com.sejong.projectservice.application.subgoal.controller.dto.SubGoalCheckResponse;
import com.sejong.projectservice.application.subgoal.controller.dto.SubGoalDeleteResponse;
import com.sejong.projectservice.application.subgoal.controller.dto.SubGoalResponse;
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
    public SubGoalCheckResponse updateCheck(String userId, Long projectId , Long subGoalId) {
        //todo userId를 통해 유저 이름 조회 근데 만약 헤더에 유저 이름도 넣는다면 안해도 된다.
        //todo project안에 협력자 및 방장만 수정 가능하도록 검증을 해야한다.
        SubGoal subGoal = subGoalRepository.findOne(subGoalId);
        subGoal.check();
        SubGoal updatedSubGoal = subGoalRepository.update(subGoal);
        return SubGoalCheckResponse.from(updatedSubGoal);
    }

    @Transactional
    public SubGoalResponse create(String userId, Long projectId, String content) {
        SubGoal subGoal = SubGoal.from(content, false, LocalDateTime.now(), LocalDateTime.now());
        SubGoal savedSubGoal = subGoalRepository.save(projectId, subGoal);
        return SubGoalResponse.from(savedSubGoal);
    }

    @Transactional
    public SubGoalDeleteResponse remove(String userId, Long projectId, Long subGoalId) {
        //todo 삭제 요청한 유저가 프로젝트에서 삭제 권한이 있는지 검증하는 로직 필요
        subGoalRepository.delete(subGoalId);
        return SubGoalDeleteResponse.of(subGoalId);
    }

    public List<SubGoal> getAll(Long projectId) {
        List<SubGoal> subGoals = subGoalRepository.getAll(projectId);
        return subGoals;
    }
}
