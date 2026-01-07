package com.sejong.projectservice.support.common.internal;

import com.sejong.projectservice.domains.project.service.ProjectService;
import com.sejong.projectservice.support.common.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.support.common.internal.response.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/project")
public class InternalController {
    private final ProjectService projectService;

    @GetMapping("/check/{postId}")
    public ResponseEntity<PostLikeCheckResponse> checkProject(@PathVariable Long postId) {
        PostLikeCheckResponse response =  projectService.checkPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorite-post")
    public ResponseEntity<ProjectResponse> getFavoriteProject(){
        ProjectResponse response = new ProjectResponse(1L);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getProjectCount() {
        Long count = projectService.getProjectCount();
        return ResponseEntity.ok(count);
    }
}
