package com.sejong.projectservice.application.internal;

import com.sejong.projectservice.application.category.service.CategoryService;
import com.sejong.projectservice.application.internal.response.PostLikeCheckResponse;
import com.sejong.projectservice.application.internal.response.ProjectResponse;
import com.sejong.projectservice.application.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {

    private final ProjectService projectService;
    private final CategoryService categoryService;

    @GetMapping("/project/check/{postId}")
    public ResponseEntity<PostLikeCheckResponse> checkProject(@PathVariable Long postId) {
        PostLikeCheckResponse response = projectService.checkPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/favorite-post")
    public ResponseEntity<ProjectResponse> getFavoriteProject() {
        ProjectResponse response = new ProjectResponse(1L);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/count")
    public ResponseEntity<Long> getProjectCount() {
        Long count = projectService.getProjectCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/category/count")
    public ResponseEntity<Long> getCategoryCount() {
        Long count = categoryService.getCategoryCount();
        return ResponseEntity.ok(count);
    }
}
