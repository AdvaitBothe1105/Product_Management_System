package com.jforce.interview2.dto;

public record AddressResponse(
        Integer id,
        String street,
        String city,
        String state,
        String pincode,
        String country,
        boolean isDefault
) {}
