package com.jforce.interview2.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Pincode is required")
        String pincode,

        @NotBlank(message = "Country is required")
        String country,

        boolean isDefault
) {
}
