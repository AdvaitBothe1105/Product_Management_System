package com.jforce.interview2.dto;

public record CategoryResponse(
        Integer id,
        String name,
        String description,
        boolean active
) {
}
