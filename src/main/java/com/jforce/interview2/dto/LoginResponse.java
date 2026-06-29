package com.jforce.interview2.dto;

public record LoginResponse(
        String token, String email, String role
) {
}
