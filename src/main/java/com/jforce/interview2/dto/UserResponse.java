package com.jforce.interview2.dto;

public record UserResponse(
        Integer id,
        String name,
        String email,
        boolean enabled,
        String role
) {
}
