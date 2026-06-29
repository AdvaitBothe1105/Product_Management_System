package com.jforce.interview2.repo;

import com.jforce.interview2.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProductId(Integer productId);
}
