package com.jforce.interview2.controller;

import com.jforce.interview2.dto.CategoryRequest;
import com.jforce.interview2.dto.CategoryResponse;
import com.jforce.interview2.model.Category;
import com.jforce.interview2.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryResponse> getActiveCategories(){
        return categoryService.getAllActiveCategories();
    }

    @GetMapping("/super-admin/categories")
    public List<CategoryResponse> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @PostMapping("/super-admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        return categoryService.createCategory(categoryRequest);
    }

    @PutMapping("/super-admin/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryRequest categoryRequest){
        return categoryService.updateCategory(id, categoryRequest);
    }

    @PatchMapping("/super-admin/categories/{id}/toggle")
    public CategoryResponse toggleCategory(@PathVariable Integer id){
        return categoryService.toggleCategory(id);
    }

    @DeleteMapping("/super-admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }

}
