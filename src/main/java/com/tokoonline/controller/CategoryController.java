package com.tokoonline.controller;

import com.tokoonline.dto.response.ApiResponse;
import com.tokoonline.entity.Category;
import com.tokoonline.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success("Berhasil", categoryService.getAllCategories()));
    }
}