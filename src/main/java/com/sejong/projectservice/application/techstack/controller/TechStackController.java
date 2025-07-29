package com.sejong.projectservice.application.techstack.controller;

import com.sejong.projectservice.application.techstack.dto.TechStackCreateReq;
import com.sejong.projectservice.application.techstack.dto.TechStackRes;
import com.sejong.projectservice.application.techstack.service.TechStackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tech-stack")
public class TechStackController {

    private final TechStackService techstackService;

    @PostMapping()
    @Operation(summary = "테크스택 생성")
    public ResponseEntity<TechStackRes> createTechStack(
            @RequestBody TechStackCreateReq techstackCreateReq
    ) {
        // TODO: 권한 검증
        TechStackRes response = techstackService.createTechStack(techstackCreateReq);
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
    @Operation(summary = " 테크스택 수정")
    public ResponseEntity<TechStackRes> updateTechStack(
            @PathVariable(name = "techStackId") Long techStackId,
            @RequestBody TechStackCreateReq techstackCreateReq
    ) {
        // TODO: 권한 검증
        TechStackRes response = techstackService.updateTechStack(techStackId, techstackCreateReq);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{techStackId}")
    @Operation(summary = "테크스택 삭제")
    public ResponseEntity<Void> deleteTechStack(
            @PathVariable(name = "techStackId") Long techStackId
    ) {
        // TODO: 권한 검증
        techstackService.deleteTechStack(techStackId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
