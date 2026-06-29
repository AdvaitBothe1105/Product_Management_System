package com.jforce.interview2.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Address is required")
        Integer addressId
) {
}
