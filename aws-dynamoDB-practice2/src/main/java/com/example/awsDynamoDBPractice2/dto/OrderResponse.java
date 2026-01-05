package com.example.awsDynamoDBPractice2.dto;

import java.util.List;

public record OrderResponse(
        String orderId,  // 주문 ID
        String status,  // 주문 상태 (ShopData.info)
        Long totalAmount,  // 총 주문 금액 (ShopData.amount)
        List<Item> items  // 주문한 상품 목록
) {}