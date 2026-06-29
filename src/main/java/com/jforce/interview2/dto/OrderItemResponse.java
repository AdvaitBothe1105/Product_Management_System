package com.jforce.interview2.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Integer orderItemId,
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal priceAtOrder,
        BigDecimal subtotal
) {
}
