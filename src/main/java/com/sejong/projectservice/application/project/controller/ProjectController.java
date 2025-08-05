package com.sejong.projectservice.application.project.controller;

import com.sejong.projectservice.application.pagination.OffsetPageReqDto;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.application.project.dto.response.*;
import com.sejong.projectservice.application.project.service.ProjectService;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.domain.ProjectDoc;
import com.sejong.projectservice.infrastructure.project.entity.ProjectDocument;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/health")
    @Operation(summary = "헬스 체크")
    public String health() {
        return "OK";
    }

    @PostMapping()
    @Operation(summary = "프로젝트 생성")
    public ResponseEntity<ProjectAddResponse> createProject(
            @RequestHeader("x-user") String userId,
            @RequestBody ProjectFormRequest projectFormRequest) {
        ProjectAddResponse response = projectService.createProject(projectFormRequest, Long.valueOf(userId));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping()
    @Operation(summary = "프로젝트 조회 (페이지네이션)") // todo: 오프셋 기반 페이지네이션 수정
    public ResponseEntity<ProjectPageResponse> getAll(
            @RequestParam(name = "size") int size,
            @RequestParam(name = "page") int page
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ProjectPageResponse response = projectService.getAllProjects(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/suggestion")
    @Operation(summary = "elastic 검색 조회 == 쿠팡 검색 추천처럼 ")
    public ResponseEntity<List<String>> getSuggestion(
            @RequestParam String query
    ) {
        List<String> suggestions = projectService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    //todo 정렬 및 desc asc 지원되게 해야됨
    @GetMapping("/search")
    @Operation(summary = "elastic 내용물 전체 조회 => 현재 정렬 방식은 지원 안함")
    public ResponseEntity<List<ProjectDoc>> searchProjects(
            @RequestParam String query,
            @RequestParam ProjectStatus projectStatus,
            @RequestParam(defaultValue ="") List<String> categories,
            @RequestParam(defaultValue ="") List<String> techStacks,
            @RequestParam(defaultValue ="5") int size,
            @RequestParam(defaultValue = "0")int page

    ) {

        List<ProjectDoc> response = projectService.searchProjects(
                query, projectStatus, categories, techStacks, size,page
        );
        return ResponseEntity.ok(response);
    }

    // todo: 커서 기반 페이지네이션 구현

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 기본 정보 수정")
    public ResponseEntity<ProjectUpdateResponse> updateProject(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody ProjectUpdateRequest projectUpdateRequest
    ) {
        ProjectUpdateResponse response = projectService.update(projectId, projectUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("{projectId}")
    @Operation(summary = "프로젝트 상세 정보 조회")
    public ResponseEntity<ProjectSpecifyInfo> specifyProject(
            @PathVariable(name = "projectId") Long projectId
    ) {
        ProjectSpecifyInfo response = projectService.findOne(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/search-page")
    @Operation(summary = "프로젝트 검색")
    public ResponseEntity<ProjectPageResponse> searchProjects(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) ProjectStatus status,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sort)
        );

        ProjectPageResponse response = projectService.search(keyword, status, pageable);
        return ResponseEntity.ok(response);
    }

    //todo 특정 사용자만 삭제할 수 있는 로직이 필요
    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제")
    public ResponseEntity<ProjectDeleteResponse> deleteProject(
            @RequestHeader("x-user") String userId,
            @PathVariable Long projectId
    ) {
        ProjectDeleteResponse response = projectService.removeProject(Long.valueOf(userId), projectId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
