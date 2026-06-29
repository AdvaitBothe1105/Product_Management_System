package com.jforce.interview2.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        boolean enabled,
        String categoryName,
        Integer quantity,
        LocalDateTime createdAt
) {
}
