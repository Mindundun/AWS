package com.example.dynamodb.dto;

import com.example.dynamodb.entity.ProductHistory;

import lombok.Getter;

@Getter
public class ProductHistoryResponse {
    private String userId;
    private String viewTime;
    private String productName;
    private Long price;

    // Entity -> DTO 변환 생성자 (Factory Method 패턴도 좋음)
    public ProductHistoryResponse(ProductHistory entity) {
        this.userId = entity.getUserId();
        this.viewTime = entity.getViewTime();
        this.productName = entity.getProductName();
        this.price = entity.getPrice();
    }
}