package com.example.awsDynamoDBPractice2.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.awsDynamoDBPractice2.dto.Item;
import com.example.awsDynamoDBPractice2.dto.OrderResponse;
import com.example.awsDynamoDBPractice2.entity.ShopData;
import com.example.awsDynamoDBPractice2.repository.ShopDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ShopDataRepository repository;

    private static final String ITEM_PREFIX = "ITEM#";

    public String createOrder(List<Item> items) {
        // 주문 ID 생성
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 4);  // 첫 4글자만 사용
        String orderId = "ORD_" + timestamp + "_" + uuid;

        // 주문 총액
        Long totalAmount = items.stream()
                .mapToLong(item -> item.price())  // .mapToLong(Item::price)
                .sum();

        // 주문 정보 + 주문 상품 합쳐서 Repository로 전달
        List<ShopData> shopDataList = new ArrayList<>();

        // 주문 정보
        ShopData orderInfo = ShopData.builder()
                .pk(orderId)
                .sk("INFO")
                .type("ORDER")
                .info("ORDER_CREATED")
                .amount(totalAmount)
                .build();
        shopDataList.add(orderInfo);

        // 주문 상품
        for (int i = 0, size = items.size(); i < size; i++) {
            Item item = items.get(i);
            ShopData itemEntity = ShopData.builder()
                    .pk(orderId)
                    .sk(ITEM_PREFIX + String.format("%03d", i + 1))
                    .type("ITEM")
                    .info(item.name())
                    .amount(item.price())
                    .build();
            shopDataList.add(itemEntity);
        }

        // 트랜잭션을 위해서 한 번에 전달
        repository.save(shopDataList);

        // 주문 ID 반환
        return orderId;
    }

    public OrderResponse getOrder(String orderId) {
        // 주문 정보 + 주문 상품 가져오기
        List<ShopData> shopDataList = repository.findById(orderId);

        // 주문 정보 찾기 (type=ORDER, sk=INFO)
        ShopData orderInfo = shopDataList.stream()
                .filter(shopData -> "ORDER".equals(shopData.getType()) && "INFO".equals(shopData.getSk()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        // 주문 상품 찾기 (type=ITEM)
        List<Item> items = shopDataList.stream()
                .filter(shopData -> "ITEM".equals(shopData.getType()))
                .map(shopData -> new Item(shopData.getInfo(), shopData.getAmount()))  // 상품명, 가격
                .toList();

        // 반환
        return new OrderResponse(
                orderId,
                orderInfo.getInfo(),  // ORDER_CREATED
                orderInfo.getAmount(),  // 주문 총액
                items
        );
    }
}
