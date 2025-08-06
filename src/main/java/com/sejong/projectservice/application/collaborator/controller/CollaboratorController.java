package com.sejong.projectservice.application.collaborator.controller;

import com.sejong.projectservice.application.collaborator.service.CollaboratorService;
import com.sejong.projectservice.core.collaborator.domain.Collaborator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collaborator")
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    @PutMapping("/{projectId}")
    @Operation(summary = "협력자들 수정")
    public ResponseEntity<List<Collaborator>> updateCollaborator(
            @RequestHeader("x-user") String userId,
            @RequestBody List<String> collaboratorNames,
            @PathVariable(name = "projectId") Long projectId

    ) {
        List<Collaborator> response = collaboratorService.updateProject(userId, projectId, collaboratorNames);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
