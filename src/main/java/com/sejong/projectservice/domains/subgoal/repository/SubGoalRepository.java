package com.sejong.projectservice.domains.subgoal.repository;

import com.sejong.projectservice.domains.subgoal.domain.SubGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubGoalRepository extends JpaRepository<SubGoalEntity, Long> {

    @Query("select sg from SubGoalEntity sg where sg.projectEntity.id = :projectId")
    List<SubGoalEntity> findAllByProjectId(@Param(value ="projectId")Long projectId);
}
