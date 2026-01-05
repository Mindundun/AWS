package com.example.awsNotifyPractice1.dto;

public record Item(
        String name,  // 상품 이름
        Long price  // 상품 가격
) {}