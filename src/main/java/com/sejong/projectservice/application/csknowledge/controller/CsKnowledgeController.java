package com.sejong.archiveservice.application.csknowledge.controller;

import com.sejong.archiveservice.application.config.security.UserContext;
import com.sejong.archiveservice.application.csknowledge.dto.CsKnowledgeReqDto;
import com.sejong.archiveservice.application.csknowledge.dto.CsKnowledgeResDto;
import com.sejong.archiveservice.application.csknowledge.service.CsKnowledgeService;
import com.sejong.archiveservice.application.pagination.CursorPageReqDto;
import com.sejong.archiveservice.application.pagination.OffsetPageReqDto;
import com.sejong.archiveservice.core.common.pagination.CursorPageResponse;
import com.sejong.archiveservice.core.common.pagination.OffsetPageResponse;
import com.sejong.archiveservice.core.csknowledge.CsKnowledge;
import com.sejong.archiveservice.core.csknowledge.TechCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cs-knowledge")
@RequiredArgsConstructor
public class CsKnowledgeController {

    private final CsKnowledgeService csKnowledgeService;

    @PostMapping
    @Operation(summary = "CS 지식 생성 ")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CsKnowledgeResDto> createCsKnowledge(@Valid @RequestBody CsKnowledgeReqDto csKnowledgeReqDto) {
        UserContext currentUser = getCurrentUser();
        CsKnowledgeResDto response = csKnowledgeService.createCsKnowledge(csKnowledgeReqDto, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{csKnowledgeId}")
    @Operation(summary = "CS 지식 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CsKnowledgeResDto> updateCsKnowledge(
            @PathVariable Long csKnowledgeId,
            @Valid @RequestBody CsKnowledgeReqDto csKnowledgeReqDto) {
        UserContext currentUser = getCurrentUser();
        CsKnowledgeResDto response = csKnowledgeService.updateCsKnowledge(csKnowledgeId, csKnowledgeReqDto, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{csKnowledgeId}")
    @Operation(summary = "CS 지식 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteCsKnowledge(@PathVariable Long csKnowledgeId) {
        UserContext currentUser = getCurrentUser();
        csKnowledgeService.deleteCsKnowledge(csKnowledgeId, currentUser.getUsername(), currentUser.getUserRole());
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
    public ResponseEntity<List<CsKnowledgeResDto>> getCsKnowledgeByCategory(@PathVariable TechCategory techCategory) {
        List<CsKnowledgeResDto> response = csKnowledgeService.findAllByTechCategory(techCategory);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unsent")
    @Operation(summary = "미전송 CS 지식 조회")
    public ResponseEntity<CsKnowledgeResDto> getUnsentKnowledge(
            @RequestParam TechCategory categoryName,
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

    private UserContext getCurrentUser() {
        return (UserContext) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}