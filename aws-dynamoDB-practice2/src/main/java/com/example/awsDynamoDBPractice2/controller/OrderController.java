package com.example.awsDynamoDBPractice2.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.awsDynamoDBPractice2.dto.CreateOrderRequest;
import com.example.awsDynamoDBPractice2.dto.CreateOrderResponse;
import com.example.awsDynamoDBPractice2.dto.OrderResponse;
import com.example.awsDynamoDBPractice2.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * [주문 생성]
     * POST /api/orders
     * Body {"items": [{"name": "PIZZA", "price": 15000}, {"name": "COKE", "price": 5000}]}
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        String newOrderId = orderService.createOrder(request.items());
        CreateOrderResponse response = new CreateOrderResponse(newOrderId, "Order Created!");
        return ResponseEntity.status(201).body(response);
    }

    /**
     * [주문 조회]
     * GET /api/orders/ORD_20250101_abcd
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}