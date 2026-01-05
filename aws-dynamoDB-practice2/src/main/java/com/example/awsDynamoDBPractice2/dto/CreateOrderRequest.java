package com.example.awsDynamoDBPractice2.dto;

import java.util.List;

public record CreateOrderRequest(
        List<Item> items  // 주문 상품 List
) {}