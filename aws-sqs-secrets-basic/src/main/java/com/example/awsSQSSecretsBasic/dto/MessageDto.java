package com.example.awsSQSSecretsBasic.dto;

public record MessageDto(
    String orderId,  // 주문 ID
    Long totalAmount, // 주문 총액
    String phone,
    String email
) {}