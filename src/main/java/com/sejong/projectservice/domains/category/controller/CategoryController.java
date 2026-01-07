package com.sejong.projectservice.domains.category.controller;

import com.sejong.projectservice.domains.category.service.CategoryService;
import com.sejong.projectservice.domains.category.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CategoryResponse> addCategory(
            @Parameter(hidden= true) @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid CategoryAddRequest categoryAddRequest
    ) {
        CategoryResponse response = categoryService.create(userRole, categoryAddRequest.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/description/{categoryId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "카테고리 관련 설명 추가")
    public ResponseEntity<CategoryResponse> updateCategoryDescription(
            @Parameter(hidden= true) @RequestHeader("X-User-Role") String userRole,
            @PathVariable(name="categoryId") Long categoryId,
            @RequestBody @Valid CategoryDescriptionRequest categoryDescriptionRequest
    ){
        CategoryResponse response = categoryService.updateDescription(userRole, categoryId,categoryDescriptionRequest.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "카테고리 수정")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(hidden= true) @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest
    ) {
        CategoryResponse response = categoryService.update(userRole, categoryUpdateRequest.getPrevName(),categoryUpdateRequest.getNextName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "카테고리 삭제")
    public ResponseEntity<CategoryResponse> deleteCategory(
            @Parameter(hidden= true)  @RequestHeader("X-User-Role") String userRole,
            @RequestBody @Valid CategoryDeleteRequest categoryDeleteRequest
    ) {
        CategoryResponse response = categoryService.remove(userRole, categoryDeleteRequest.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    @Operation(summary = "카테고리 전체 조회")
    public ResponseEntity<CategoryAllResponse> getAllCategory(){
        CategoryAllResponse response = categoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{postId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "프로젝트의 카테고리 수정")
    public ResponseEntity<CategoryAllResponse> updateProjectCategory(
            @Parameter(hidden= true) @RequestHeader("X-User-Id") String username,
            @PathVariable(name="postId") Long projectId,
            @RequestParam List<String> categoryNames
    ) {
        CategoryAllResponse response = categoryService.updateProject(username, projectId, categoryNames);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
