package com.example.awsDynamoDBPractice2.dto;

public record CreateOrderResponse(
        String orderId,  // 주문 ID
        String message  // 주문 메시지
) {}