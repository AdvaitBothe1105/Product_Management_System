package com.jforce.interview2.repo;

import com.jforce.interview2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryIdAndEnabledTrue(Integer categoryId);

    // user — view all active products
    List<Product> findByEnabledTrue();

    // admin — view all products regardless of status
    List<Product> findByCategoryId(Integer categoryId);

    // search by name
    List<Product> findByNameContainingIgnoreCaseAndEnabledTrue(String name);
}
