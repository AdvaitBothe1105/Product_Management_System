package com.jforce.interview2.controller;

import com.jforce.interview2.dto.CheckoutRequest;
import com.jforce.interview2.dto.OrderResponse;
import com.jforce.interview2.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/orders/checkout")
    public OrderResponse checkout(
            @Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(request);
    }

    @GetMapping("/orders")
    public List<OrderResponse> getMyOrders() {
        return orderService.getMyOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getMyOrder(@PathVariable Integer id) {
        return orderService.getMyOrderById(id);
    }

    @PatchMapping("/orders/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Integer id) {
        return orderService.cancelOrder(id);
    }


    @GetMapping("/super-admin/orders")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/super-admin/orders/status/{status}")
    public List<OrderResponse> getByStatus(@PathVariable String status) {
        return orderService.getOrdersByStatus(status);
    }

    @PatchMapping("/super-admin/orders/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }
}
