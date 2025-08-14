package com.sejong.projectservice.application.category.controller;

import com.sejong.projectservice.application.category.controller.dto.*;
import com.sejong.projectservice.application.category.service.CategoryService;
import com.sejong.projectservice.core.category.Category;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("")
    @Operation(summary = "카테고리 저장")
    public ResponseEntity<CategoryResponse> addCategory(
            @RequestHeader("x-user") String userId,
            @RequestBody @Valid CategoryAddRequest categoryAddRequest
    ) {
        CategoryResponse response = categoryService.create(userId, categoryAddRequest.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("")
    @Operation(summary = "카테고리 수정")
    public ResponseEntity<CategoryResponse> updateCategory(
            @RequestHeader("x-user") String userId,
            @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest
    ) {
        CategoryResponse response = categoryService.update(userId, categoryUpdateRequest.getPrevName(),categoryUpdateRequest.getNextName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("")
    @Operation(summary = "카테고리 삭제")
    public ResponseEntity<CategoryResponse> deleteCategory(
            @RequestHeader("x-user") String userId,
            @RequestBody @Valid CategoryDeleteRequest categoryDeleteRequest
    ) {
        CategoryResponse response = categoryService.remove(userId, categoryDeleteRequest.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    @Operation(summary = "카테고리 전체 조회")
    public ResponseEntity<CategoryAllResponse> getAllCategory(){
        CategoryAllResponse response = categoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "프로젝트의 카테고리 수정")
    public ResponseEntity<CategoryAllResponse> updateProjectCategory(
            @RequestHeader("x-user-name") String userName,
            @PathVariable(name="postId") Long projectId,
            @RequestParam List<String> categoryNames
    ) {
        CategoryAllResponse response = categoryService.updateProject(userName, projectId, categoryNames);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
