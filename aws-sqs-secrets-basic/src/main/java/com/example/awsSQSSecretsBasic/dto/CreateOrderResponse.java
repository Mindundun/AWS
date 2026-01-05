package com.example.awsSQSSecretsBasic.dto;

public record CreateOrderResponse(
        String orderId,  // 주문 ID
        Long totalAmount, // 주문 총액
        String message  // 주문 메시지
) {}