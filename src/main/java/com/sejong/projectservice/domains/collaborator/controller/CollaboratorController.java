package com.sejong.projectservice.domains.collaborator.controller;


import com.sejong.projectservice.domains.collaborator.domain.CollaboratorDto;
import com.sejong.projectservice.domains.collaborator.service.CollaboratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "협력자들 수정 ")
    public ResponseEntity<List<CollaboratorDto>> updateCollaborator(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @RequestBody List<String> collaboratorNames,
            @PathVariable(name = "projectId") Long projectId

    ) {
        List<CollaboratorDto> response = collaboratorService.updateProject(username, projectId, collaboratorNames);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
