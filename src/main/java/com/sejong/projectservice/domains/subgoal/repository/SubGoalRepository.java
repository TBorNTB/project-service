package com.sejong.projectservice.domains.subgoal.repository;

import com.sejong.projectservice.domains.subgoal.domain.SubGoalDto;

import java.util.List;

public interface SubGoalRepository {
    SubGoalDto findOne(Long subGoalId);

    SubGoalDto update(SubGoalDto subGoalDto);

    SubGoalDto save(Long projectId, SubGoalDto subGoalDto);

    void delete(Long subGoalId);

    List<SubGoalDto> getAll(Long projectId);
}
