package com.jforce.interview2.repo;

import com.jforce.interview2.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserId(Integer userId);
}
