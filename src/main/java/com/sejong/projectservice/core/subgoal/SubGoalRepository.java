package com.sejong.projectservice.core.subgoal;

import java.util.List;

public interface SubGoalRepository {
    SubGoal findOne(Long subGoalId);

    SubGoal update(SubGoal subGoal);

    SubGoal save(Long projectId, SubGoal subGoal);

    void delete(Long subGoalId);

    List<SubGoal> getAll(Long projectId);
}
