package com.example.dynamodb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamodb.dto.ProductHistoryResponse;
import com.example.dynamodb.dto.ProductHistoryCreateRequest;
import com.example.dynamodb.service.ProductHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class ProductHistoryController {

    private final ProductHistoryService historyService;

    // [저장 요청]
    // POST /history
    // Body {"userId": "u100", "productId": "p100", "productName": "MacBook", "price": 300}
    @PostMapping
    public String save(@RequestBody ProductHistoryCreateRequest request) {
        historyService.saveHistory(
                request.userId(),
                request.productId(),
                request.productName(),
                request.price()
        );
        return "Saved!";
    }

    // [이력 조회 요청]
    // GET /history/{userId}
    @GetMapping("/{userId}")
    public List<ProductHistoryResponse> getHistories(@PathVariable String userId) {
        return historyService.getUserHistories(userId);
    }
}