package com.sejong.projectservice.domains.subgoal.repository;

import com.sejong.projectservice.domains.subgoal.domain.SubGoal;

import java.util.List;

public interface SubGoalRepository {
    SubGoal findOne(Long subGoalId);

    SubGoal update(SubGoal subGoal);

    SubGoal save(Long projectId, SubGoal subGoal);

    void delete(Long subGoalId);

    List<SubGoal> getAll(Long projectId);
}
