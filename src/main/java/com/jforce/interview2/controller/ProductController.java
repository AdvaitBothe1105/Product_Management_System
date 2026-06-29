package com.jforce.interview2.controller;

import com.jforce.interview2.dto.ProductRequest;
import com.jforce.interview2.dto.ProductResponse;
import com.jforce.interview2.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public List<ProductResponse> getAllActiveProducts(){
        return productService.getAllActiveProducts();
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }
    @GetMapping("/products/category/{id}")
    public List<ProductResponse> getProductsByCategory(@PathVariable Integer id) {
        return productService.getProductsByCategory(id);
    }

    @GetMapping("/products/search")
    public List<ProductResponse> getProductsByName(@RequestBody String name){
        return productService.searchProducts(name);
    }

    //admin endpoints

    @GetMapping("/admin/products")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/admin/products/category/{categoryId}")
    public List<ProductResponse> getAllByCategory(
            @PathVariable Integer categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @PostMapping("/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/admin/products/{id}")
    public ProductResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @PatchMapping("/admin/products/{id}/price")
    public ProductResponse updatePrice(
            @PathVariable Integer id,
            @RequestParam BigDecimal price) {
        return productService.updatePrice(id, price);
    }

    @PatchMapping("/admin/products/{id}/inventory")
    public ProductResponse updateInventory(
            @PathVariable Integer id,
            @RequestParam Integer quantity) {
        return productService.updateInventory(id, quantity);
    }

    @PatchMapping("/admin/products/{id}/toggle")
    public ProductResponse toggle(@PathVariable Integer id) {
        return productService.toggleProduct(id);
    }

    @PatchMapping("/admin/products/{id}/category/{categoryId}")
    public ProductResponse assignCategory(
            @PathVariable Integer id,
            @PathVariable Integer categoryId) {
        return productService.assignCategory(id, categoryId);
    }
}
