package com.jforce.interview2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Integer orderId,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String status,
        String deliveryAddress,
        LocalDateTime createdAt
) {
}
