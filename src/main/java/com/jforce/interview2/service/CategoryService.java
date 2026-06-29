package com.jforce.interview2.service;

import com.jforce.interview2.dto.CategoryRequest;
import com.jforce.interview2.dto.CategoryResponse;
import com.jforce.interview2.model.Category;
import com.jforce.interview2.model.User;
import com.jforce.interview2.repo.CategoryRepo;
import com.jforce.interview2.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    private CategoryResponse toDto(Category category){
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );
    }

    public List<CategoryResponse> getAllActiveCategories(){
        return categoryRepo.findByActiveTrue()
                .stream().map(this::toDto).toList();
    }

    public List<CategoryResponse> getAllCategories(){
        return categoryRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }


    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepo.existsByName(request.name())){
            throw new RuntimeException("Category already exists");
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();
        return toDto(categoryRepo.save(category));
    }

    public CategoryResponse updateCategory(Integer id,CategoryRequest request){
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getName().equals(request.name())
                && categoryRepo.existsByName(request.name())) {
            throw new RuntimeException("Category name already taken: "
                    + request.name());
        }

        category.setName(request.name());
        category.setDescription(request.description());

        return toDto(categoryRepo.save(category));
    }

    public CategoryResponse toggleCategory(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setActive(!category.isActive());
        return toDto(categoryRepo.save(category));
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if(!category.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing products. " + "Disable it instead");
        }
        categoryRepo.delete(category);
    }
}
