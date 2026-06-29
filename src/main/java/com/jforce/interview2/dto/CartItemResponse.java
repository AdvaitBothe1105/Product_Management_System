package com.jforce.interview2.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Integer cartItemId,
        Integer productId,
        String productName,
        BigDecimal priceAtAddition,
        Integer quantity,
        BigDecimal subtotal
) {
}
