package com.sejong.projectservice.domains.csknowledge.controller;


import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.projectservice.domains.csknowledge.dto.CsKnowledgeResDto;
import com.sejong.projectservice.domains.csknowledge.service.CsKnowledgeService;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.pagination.CursorPageResponse;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cs-knowledge")
@RequiredArgsConstructor
public class CsKnowledgeController {

    private final CsKnowledgeService csKnowledgeService;

    @PostMapping
    @Operation(summary = "CS 지식 생성 ")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CsKnowledgeResDto> createCsKnowledge(
            @Valid @RequestBody CsKnowledgeReqDto csKnowledgeReqDto,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username
    ) {
        CsKnowledgeResDto response = csKnowledgeService.createCsKnowledge(csKnowledgeReqDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{csKnowledgeId}")
    @Operation(summary = "CS 지식 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CsKnowledgeResDto> updateCsKnowledge(
            @PathVariable Long csKnowledgeId,
            @Valid @RequestBody CsKnowledgeReqDto csKnowledgeReqDto,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username) {
        CsKnowledgeResDto response = csKnowledgeService.updateCsKnowledge(csKnowledgeId, csKnowledgeReqDto, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{csKnowledgeId}")
    @Operation(summary = "CS 지식 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteCsKnowledge(
            @PathVariable Long csKnowledgeId,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @Parameter(hidden = true) @RequestHeader("X-User-Role") String userRole) {

        csKnowledgeService.deleteCsKnowledge(csKnowledgeId, username, userRole);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{csKnowledgeId}")
    @Operation(summary = "CS 지식 조회")
    public ResponseEntity<CsKnowledgeResDto> getCsKnowledge(@PathVariable Long csKnowledgeId) {
        CsKnowledgeResDto response = csKnowledgeService.findById(csKnowledgeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{csKnowledgeId}/exists")
    @Operation(summary = "CS 지식 존재 여부 확인")
    public ResponseEntity<Boolean> checkCsKnowledgeExists(@PathVariable Long csKnowledgeId) {
        Boolean exists = csKnowledgeService.exists(csKnowledgeId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/category/{techCategory}")
    @Operation(summary = "카테고리별 CS 지식 조회")
    public ResponseEntity<List<CsKnowledgeResDto>> getCsKnowledgeByCategory(@PathVariable("techCategory") String categoryName) {
        List<CsKnowledgeResDto> response = csKnowledgeService.findAllByTechCategory(categoryName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unsent")
    @Operation(summary = "미전송 CS 지식 조회")
    public ResponseEntity<CsKnowledgeResDto> getUnsentKnowledge(
            @RequestParam String categoryName,
            @RequestParam String email) {
        Optional<CsKnowledgeResDto> unsentKnowledge = csKnowledgeService.findUnsentKnowledge(categoryName, email);
        return unsentKnowledge
                .map(knowledge -> ResponseEntity.ok(unsentKnowledge.get()))
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/offset")
    @Operation(summary = "오프셋 페이지네이션으로 CS 지식 목록 조회")
    public ResponseEntity<OffsetPageResponse<List<CsKnowledgeResDto>>> getOffsetCsKnowledge(
            @ParameterObject @Valid OffsetPageReqDto offsetPageReqDto) {
        OffsetPageResponse<List<CsKnowledgeResDto>> response = csKnowledgeService.getOffsetCsKnowledge(offsetPageReqDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cursor")
    @Operation(summary = "커서 페이지네이션으로 CS 지식 목록 조회")
    public ResponseEntity<CursorPageResponse<List<CsKnowledgeResDto>>> getCursorCsKnowledge(
            @ParameterObject @Valid CursorPageReqDto cursorPageReqDto) {
        CursorPageResponse<List<CsKnowledgeResDto>> response = csKnowledgeService.getCursorCsKnowledge(cursorPageReqDto);
        return ResponseEntity.ok(response);
    }
}