package com.jforce.interview2.repo;

import com.jforce.interview2.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByActiveTrue();
}
