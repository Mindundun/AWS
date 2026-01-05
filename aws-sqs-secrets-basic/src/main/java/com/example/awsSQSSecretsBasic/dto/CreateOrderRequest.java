package com.example.awsSQSSecretsBasic.dto;

import com.example.awsSQSSecretsBasic.dto.Item;

import java.util.List;

public record CreateOrderRequest(
        List<Item> items,  // 주문 상품 List
        String email,
        String phone
) {}