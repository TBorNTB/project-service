package com.sejong.projectservice.application.external;

import com.sejong.projectservice.application.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/external/project")
public class ProjectExternalController {
    private final ProjectService projectService;

    @GetMapping("/check/{postId}")
    public ResponseEntity<Boolean> checkProject(@PathVariable Long postId) {
        boolean exists = projectService.exists(postId);
        return ResponseEntity.ok(exists);
    }
}
