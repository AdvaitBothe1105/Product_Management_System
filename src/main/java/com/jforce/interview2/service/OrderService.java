package com.jforce.interview2.service;

import com.jforce.interview2.dto.CheckoutRequest;
import com.jforce.interview2.dto.OrderItemResponse;
import com.jforce.interview2.dto.OrderResponse;
import com.jforce.interview2.model.*;
import com.jforce.interview2.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final InventoryRepo inventoryRepo;
    private final AddressRepo addressRepo;
    private final UserRepo userRepo;
    private final CartService cartService;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private OrderItemResponse toItemDto(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtOrder(),
                item.getSubtotal()
        );
    }

    private OrderResponse toDto(Order order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemDto)
                .toList();

        // format address as single string
        Address addr = order.getDeliveryAddress();
        String fullAddress = addr.getStreet() + ", "
                + addr.getCity() + ", "
                + addr.getState() + " - "
                + addr.getPincode() + ", "
                + addr.getCountry();

        return new OrderResponse(
                order.getId(),
                items,
                order.getTotalAmount(),
                order.getStatus().name(),
                fullAddress,
                order.getCreatedAt()
        );
    }


    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        User user = getLoggedInUser();

        // Step 1 — get cart
        Cart cart = cartRepo.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        // Step 2 — validate cart
        // checks: not empty, all products enabled, sufficient inventory
        cartService.validateCart(cart);

        // Step 3 — validate delivery address
        Address address = addressRepo.findById(request.addressId())
                .orElseThrow(() ->
                        new RuntimeException("Address not found: "
                                + request.addressId()));

        // make sure address belongs to logged in user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized — address does not belong to you");
        }

        // Step 4 — create order
        Order order = Order.builder()
                .user(user)
                .deliveryAddress(address)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO) // calculated below
                .items(new ArrayList<>())
                .build();

        Order savedOrder = orderRepo.save(order);

        // Step 5 — create order items + reduce inventory
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            // reduce inventory
            Inventory inventory = inventoryRepo
                    .findByProductId(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Inventory not found"));

            // double check inventory (race condition protection)
            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for: "
                                + cartItem.getProduct().getName()
                );
            }

            inventory.setQuantity(
                    inventory.getQuantity() - cartItem.getQuantity()
            );
            inventoryRepo.save(inventory);

            // calculate subtotal
            BigDecimal subtotal = cartItem.getPriceAtAddition()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalAmount = totalAmount.add(subtotal);

            // create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtOrder(cartItem.getPriceAtAddition())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
        }

        // Step 6 — set total and items on order
        savedOrder.setItems(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setStatus(OrderStatus.CONFIRMED);
        orderRepo.save(savedOrder);

        // Step 7 — clear cart after successful order
        cartItemRepo.deleteByCartId(cart.getId());

        return toDto(savedOrder);
    }


    public List<OrderResponse> getMyOrders() {
        User user = getLoggedInUser();
        return orderRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public OrderResponse getMyOrderById(Integer orderId) {
        User user = getLoggedInUser();

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));

        // make sure order belongs to logged in user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return toDto(order);
    }

    public OrderResponse cancelOrder(Integer orderId) {
        User user = getLoggedInUser();

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // only PENDING orders can be cancelled
        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Cannot cancel order with status: "
                            + order.getStatus().name()
            );
        }

        // restore inventory on cancellation
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepo
                    .findByProductId(item.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Inventory not found"));

            inventory.setQuantity(
                    inventory.getQuantity() + item.getQuantity()
            );
            inventoryRepo.save(inventory);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toDto(orderRepo.save(order));
    }


    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<OrderResponse> getOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepo.findByStatus(orderStatus)
                    .stream()
                    .map(this::toDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    public OrderResponse updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + orderId));

        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        return toDto(orderRepo.save(order));
    }
}
