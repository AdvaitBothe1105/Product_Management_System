package com.jforce.interview2.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleUpdateRequest(
        @NotBlank String role
) {
}
