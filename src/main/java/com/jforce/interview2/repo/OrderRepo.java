package com.jforce.interview2.repo;

import com.jforce.interview2.model.Order;
import com.jforce.interview2.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);

    // user — my orders sorted newest first
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // super admin — all orders by status
    List<Order> findByStatus(OrderStatus status);

    // super admin — all orders sorted newest first
    List<Order> findAllByOrderByCreatedAtDesc();
}
