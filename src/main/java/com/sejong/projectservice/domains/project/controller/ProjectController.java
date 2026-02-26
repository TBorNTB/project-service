package com.sejong.projectservice.domains.project.controller;

import com.sejong.projectservice.domains.project.dto.request.DateCountRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.domains.project.dto.request.ProjectUpdateRequest;
import com.sejong.projectservice.domains.project.dto.response.DateCountResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectDeleteResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.domains.project.dto.response.ProjectSpecifyInfoRes;
import com.sejong.projectservice.domains.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.domains.project.service.ProjectService;
import com.sejong.projectservice.support.common.constants.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/health")
    @Operation(summary = "헬스 체크")
    public String health() {
        return "OK";
    }

    @PostMapping()
    @Operation(summary = "프로젝트 생성")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProjectAddResponse> createProject(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @RequestBody ProjectFormRequest projectFormRequest) {
        log.info("username = {}", username);
        ProjectAddResponse response = projectService.createProject(projectFormRequest, username);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping()
    @Operation(summary = "프로젝트 조회 (페이지네이션)") // todo: 오프셋 기반 페이지네이션 수정
    @SecurityRequirement(name = "bearerAuth")
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

    // todo: 커서 기반 페이지네이션 구현

    @PutMapping("/{projectId}")
    @Operation(summary = "프로젝트 기본 정보 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProjectUpdateResponse> updateProject(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody ProjectUpdateRequest projectUpdateRequest
    ) {
        ProjectUpdateResponse response = projectService.update(projectId, projectUpdateRequest, username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("{projectId}")
    @Operation(summary = "프로젝트 상세 정보 조회")
    public ResponseEntity<ProjectSpecifyInfoRes> specifyProject(
            @PathVariable(name = "projectId") Long projectId
    ) {
        ProjectSpecifyInfoRes response = projectService.findOne(projectId);
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

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProjectDeleteResponse> deleteProject(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username,
            @Parameter(hidden = true) @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long projectId
    ) {
        ProjectDeleteResponse response = projectService.removeProject(username, projectId, userRole);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/counts")
    @Operation(summary = "특정 기간의 cs, news, project 수 조회")
    public ResponseEntity<DateCountResponse> getCountsByDate(
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        DateCountRequest request = DateCountRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        DateCountResponse response = projectService.getCountsByDate(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
