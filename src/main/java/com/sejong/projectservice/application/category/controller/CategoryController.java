package com.sejong.projectservice.application.category.controller;

import com.sejong.projectservice.application.category.controller.dto.*;
import com.sejong.projectservice.application.category.service.CategoryService;
import com.sejong.projectservice.core.category.Category;
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
    public ResponseEntity<CategoryResponse> addCategory(
            @RequestHeader("x-user") String userId,
            @RequestBody @Valid CategoryAddRequest categoryAddRequest
    ) {
        CategoryResponse response = categoryService.create(userId, categoryAddRequest.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("")
    public ResponseEntity<CategoryResponse> updateCategory(
            @RequestHeader("x-user") String userId,
            @RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest
    ) {
        CategoryResponse response = categoryService.update(userId, categoryUpdateRequest.getPrevName(),categoryUpdateRequest.getNextName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("")
    public ResponseEntity<CategoryResponse> deleteCategory(
            @RequestHeader("x-user") String userId,
            @RequestBody @Valid CategoryDeleteRequest categoryDeleteRequest
    ) {
        CategoryResponse response = categoryService.remove(userId, categoryDeleteRequest.getName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<CategoryAllResponse> getAllCategory(){
        CategoryAllResponse response = categoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
