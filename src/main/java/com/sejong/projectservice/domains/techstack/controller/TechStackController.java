package com.sejong.projectservice.domains.techstack.controller;

import com.sejong.projectservice.domains.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.domains.techstack.dto.TechStackRes;
import com.sejong.projectservice.domains.techstack.service.TechStackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tech-stack")
public class TechStackController {

    private final TechStackService techstackService;

    @PostMapping()
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "테크스택 생성")
    public ResponseEntity<TechStackRes> createTechStack(
           @Parameter(hidden= true) @RequestHeader("X-User-Role") String userRole,
            @RequestBody TechStackCreateReq techstackCreateReq
    ) {
        // TODO: 권한 검증
        TechStackRes response = techstackService.createTechStack(techstackCreateReq,userRole);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{techStackId}")
    @Operation(summary = "테크스택 조회")
    public ResponseEntity<TechStackRes> getTechStack(
            @PathVariable(name = "techStackId") Long techStackId
    ) {
        TechStackRes response = techstackService.getTechStack(techStackId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{techStackId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = " 테크스택 수정")
    public ResponseEntity<TechStackRes> updateTechStack(
            @Parameter(hidden= true) @RequestHeader("X-User-Role") String userRole,
            @PathVariable(name = "techStackId") Long techStackId,
            @RequestBody TechStackCreateReq techstackCreateReq
    ) {
        // TODO: 권한 검증
        TechStackRes response = techstackService.updateTechStack(techStackId, techstackCreateReq,userRole);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{techStackId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "테크스택 삭제")
    public ResponseEntity<Void> deleteTechStack(
            @Parameter(hidden= true) @RequestHeader("X-User-Role") String userRole,
            @PathVariable(name = "techStackId") Long techStackId
    ) {
        // TODO: 권한 검증
        techstackService.deleteTechStack(techStackId,userRole);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
