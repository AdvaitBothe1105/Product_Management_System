package com.jforce.interview2.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Integer cartId,
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        Integer totalItems
) {
}
