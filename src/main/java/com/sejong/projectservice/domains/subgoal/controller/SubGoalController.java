package com.sejong.projectservice.domains.subgoal.controller;

import com.sejong.projectservice.domains.subgoal.dto.SubGoalCheckResponse;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDeleteResponse;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalRequest;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalResponse;
import com.sejong.projectservice.domains.subgoal.service.SubGoalService;
import com.sejong.projectservice.domains.subgoal.dto.SubGoalDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subgoal")
public class SubGoalController {
    private final SubGoalService subGoalService;

    @PutMapping("/check/{projectId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary ="서브 목표 달성 완료 기능")
    public ResponseEntity<SubGoalCheckResponse> checkSubGoal(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name="projectId") Long projectId,
            @RequestParam(name = "subGoalId") Long subGoalId
    ){
        SubGoalCheckResponse response = subGoalService.updateCheck(username,projectId, subGoalId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary ="서브 목표 추가")
    public ResponseEntity<SubGoalResponse> addSubGoal(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name="projectId") Long projectId,
            @RequestBody SubGoalRequest subGoalRequest
    ){
        SubGoalResponse response = subGoalService.create(username,projectId, subGoalRequest.getContent());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}/{subGoalId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "서브 목표 삭제")
    public ResponseEntity<SubGoalDeleteResponse> deleteSubGoal(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name="projectId") Long projectId,
            @PathVariable(name="subGoalId") Long subGoalId
    ){
        SubGoalDeleteResponse response = subGoalService.remove(username, projectId, subGoalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}")
    @Operation(summary ="서브 목표 전체 조회")
    public ResponseEntity<List<SubGoalDto>> getAllSubGoals(
            @PathVariable(name ="projectId") Long projectId
    ){
        List<SubGoalDto> response= subGoalService.getAll(projectId);
        return ResponseEntity.ok(response);
    }

 }
