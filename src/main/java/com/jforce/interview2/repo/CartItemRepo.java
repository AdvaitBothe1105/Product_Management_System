package com.jforce.interview2.repo;

import com.jforce.interview2.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
    List<CartItem> findByCartId(Integer cartId);
    void deleteByCartId(Integer cartId);
}

