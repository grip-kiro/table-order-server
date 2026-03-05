package com.tableorder.customer.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        Long storeId,
        Long tableId,
        Integer tableNumber,
        String sessionId
) {}
