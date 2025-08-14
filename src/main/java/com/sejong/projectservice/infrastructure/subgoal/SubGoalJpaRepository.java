package com.sejong.projectservice.infrastructure.subgoal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubGoalJpaRepository extends JpaRepository<SubGoalEntity, Long> {

    @Query("select sg from SubGoalEntity sg where sg.projectEntity.id = :projectId")
    List<SubGoalEntity> findAllByProjectId(@Param(value ="projectId")Long projectId);
}
