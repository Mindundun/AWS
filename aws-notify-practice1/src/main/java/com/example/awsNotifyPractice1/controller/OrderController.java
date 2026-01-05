package com.example.awsNotifyPractice1.controller;

import com.example.awsNotifyPractice1.dto.CreateOrderRequest;
import com.example.awsNotifyPractice1.dto.CreateOrderResponse;
import com.example.awsNotifyPractice1.dto.OrderResponse;
import com.example.awsNotifyPractice1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(orderService.createOrder(request.items()));
    }

    /**
     * [주문 생성]
     * POST /api/orders
     * Body {"items": [{"name": "PIZZA", "price": 15000}, {"name": "COKE", "price": 5000}]}
     */
    @PostMapping("/Email")
    public ResponseEntity<CreateOrderResponse> createOrderEmail(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrderEmail(request));
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