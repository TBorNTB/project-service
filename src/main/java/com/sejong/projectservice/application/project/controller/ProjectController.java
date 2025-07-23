package com.sejong.projectservice.application.project.controller;

import com.sejong.projectservice.application.project.dto.request.ProjectFormRequest;
import com.sejong.projectservice.application.project.dto.response.ProjectAddResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectPageResponse;
import com.sejong.projectservice.application.project.dto.response.ProjectSpecifyInfo;
import com.sejong.projectservice.application.project.dto.response.ProjectUpdateResponse;
import com.sejong.projectservice.application.project.service.ProjectService;
import com.sejong.projectservice.core.enums.Category;
import com.sejong.projectservice.core.enums.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      @RequestBody ProjectFormRequest projectFormRequest) {
    ProjectAddResponse response = projectService.register(projectFormRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @GetMapping("/all")
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

  @PutMapping("/{projectId}")
  public ResponseEntity<ProjectUpdateResponse> updateProject(
      @PathVariable(name = "projectId") Long projectId,
      @RequestBody ProjectFormRequest projectFormRequest
  ) {
    ProjectUpdateResponse response = projectService.update(projectId, projectFormRequest);
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
    Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(Sort.Direction.fromString(direction), sort)
    );

    ProjectPageResponse response = projectService.search(keyword, category, status, pageable);
    return ResponseEntity.ok(response);
  }
}
