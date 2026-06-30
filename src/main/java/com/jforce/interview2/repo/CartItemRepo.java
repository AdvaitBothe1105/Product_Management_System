package com.jforce.interview2.repo;

import com.jforce.interview2.model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
    List<CartItem> findByCartId(Integer cartId);
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.cart.id = :cartId")
    void deleteByCartId(Integer cartId);
}

