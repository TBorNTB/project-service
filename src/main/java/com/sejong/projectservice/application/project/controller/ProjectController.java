package com.sejong.projectservice.application.project.controller;

import com.sejong.projectservice.application.mapper.ProjectCommandMapper;
import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.application.project.service.ProjectService;
import com.sejong.projectservice.core.common.PageSearchCommand;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import com.sejong.projectservice.core.project.command.ProjectFormCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/add")
    public ResponseEntity<ProjectAddResponse> add(
            @RequestBody ProjectFormRequest projectFormRequest,
            @RequestHeader("X-User-ID") String userId) {
        ProjectFormCommand projectFormCommand = ProjectCommandMapper.toCommand(projectFormRequest);
        ProjectAddResponse response = projectService.register(projectFormCommand, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ProjectPageResponse> getAll(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {

        PageSearchCommand pageSearchCommand = PageSearchCommand.of(size, page, "createdAt", "desc");
        ProjectPageResponse response = projectService.getAllProjects(pageSearchCommand);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectUpdateResponse> updateProject(
            @PathVariable(name = "projectId") Long projectId,
            @RequestBody ProjectFormRequest projectFormRequest
    ) {
        ProjectFormCommand projectFormCommand = ProjectCommandMapper.toCommand(projectFormRequest);
        ProjectUpdateResponse response = projectService.update(projectId, projectFormCommand);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("{projectId}")
    public ResponseEntity<ProjectSpecifyInfo> specifyProject(
            @PathVariable(name = "projectId") Long projectId
    ) {
        ProjectSpecifyInfo response = projectService.findOne(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/search")
    public ResponseEntity<ProjectPageResponse> searchProjects(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "status", required = false) ProjectStatus status,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageSearchCommand pageSearchCommand = PageSearchCommand.of(size, page, sort, direction);
        ProjectPageResponse response = projectService.search(keyword, category, status, pageSearchCommand);
        return ResponseEntity.ok(response);
    }
}
