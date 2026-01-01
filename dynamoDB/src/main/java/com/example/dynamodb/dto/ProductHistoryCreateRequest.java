package com.example.dynamodb.dto;

public record ProductHistoryCreateRequest(
        String userId,
        String productId,
        String productName,
        Long price
) {}