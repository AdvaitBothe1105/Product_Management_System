package com.jforce.interview2.service;

import com.jforce.interview2.dto.ProductRequest;
import com.jforce.interview2.dto.ProductResponse;
import com.jforce.interview2.model.Category;
import com.jforce.interview2.model.Inventory;
import com.jforce.interview2.model.Product;
import com.jforce.interview2.repo.CategoryRepo;
import com.jforce.interview2.repo.InventoryRepo;
import com.jforce.interview2.repo.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final InventoryRepo inventoryRepo;

    private ProductResponse toDto(Product product) {
        Integer qty = product.getInventory() != null ? product.getInventory().getQuantity() : 0;
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isEnabled(),
                product.getCategory().getName(),
                qty,
                product.getCreatedAt()
        );
    }

    //USER ENDPOINTS
    public List<ProductResponse> getAllActiveProducts(){
        return productRepo.findByEnabledTrue()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ProductResponse> getActiveProductsByCategory(Integer categoryId) {
         categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
         return productRepo.findByCategoryIdAndEnabledTrue(categoryId)
                 .stream()
                 .map(this::toDto)
                 .toList();
    }

    public ProductResponse getProductById(Integer productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId));

        if (!product.isEnabled()) {
            throw new RuntimeException("Product is not available");
        }

        return toDto(product);
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepo.findByNameContainingIgnoreCaseAndEnabledTrue(name)
                .stream().map(this::toDto).toList();
    }

    //Admin endpoints
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // get all products by category (admin — includes disabled)
    public List<ProductResponse> getProductsByCategory(Integer categoryId) {
        return productRepo.findByCategoryId(categoryId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    //create product
    @Transactional
    public ProductResponse createProduct(ProductRequest request){
        Category category = categoryRepo.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category does not exists"));

        if(!category.isActive()) {
            throw new RuntimeException("Cannot add products to inactive categories");
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(category)
                .enabled(true)
                .build();
        Product saved = productRepo.save(product);

        Inventory inventory = Inventory.builder()
                .product(saved)
                .quantity(request.quantity())
                .build();
        inventoryRepo.save(inventory);
        saved.setInventory(inventory);

        return toDto(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id));

        Category category = categoryRepo.findById(request.categoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found: "
                                + request.categoryId()));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);

        // update inventory quantity
        Inventory inventory = inventoryRepo.findByProductId(id)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found for product: " + id));

        inventory.setQuantity(request.quantity());
        inventoryRepo.save(inventory);

        return toDto(productRepo.save(product));
    }

    // update price only
    public ProductResponse updatePrice(Integer id, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id));

        product.setPrice(price);
        return toDto(productRepo.save(product));
    }

    // update inventory quantity only
    public ProductResponse updateInventory(Integer id, Integer quantity) {
        if (quantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }

        Inventory inventory = inventoryRepo.findByProductId(id)
                .orElseThrow(() ->
                        new RuntimeException("Inventory not found for product: " + id));

        inventory.setQuantity(quantity);
        inventoryRepo.save(inventory);

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id));

        return toDto(product);
    }

    // enable or disable product
    public ProductResponse toggleProduct(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id));

        product.setEnabled(!product.isEnabled());
        return toDto(productRepo.save(product));
    }

    // assign product to different category
    public ProductResponse assignCategory(Integer productId, Integer categoryId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId));

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found: " + categoryId));

        if (!category.isActive()) {
            throw new RuntimeException("Cannot assign to inactive category");
        }

        product.setCategory(category);
        return toDto(productRepo.save(product));
    }

}
