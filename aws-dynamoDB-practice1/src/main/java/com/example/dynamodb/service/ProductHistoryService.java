package com.example.dynamodb.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.dynamodb.dto.ProductHistoryResponse;
import com.example.dynamodb.entity.ProductHistory;
import com.example.dynamodb.repository.ProductHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductHistoryService {

    private final ProductHistoryRepository repository;

    /**
     * 상품 조회 이력 저장
     * 찰나의 순간(동일 ms)에 발생하는 중복 저장을 방지하기 위해
     * SK(viewTime) 생성 시 유니크한 식별자 조합 등을 권장합니다.
     * 이 예시는 제품을 조회한 시간에 제품 ID를 추가하여 중복 저장을 회피했습니다.
     */
    public void saveHistory(String userId, String productId, String productName, Long price) {
        // ISO-8601 포맷 - 문자열이 날짜와 일치하는 정렬 순서를 가짐 (Long 타입 날짜가 아니라면 추천)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        // SK: 시간#제품ID로 결정
        String sortKey = timestamp + "#" + productId;
        ProductHistory productHistory = new ProductHistory(userId, sortKey, productId, productName, price);
        repository.save(productHistory);
    }

    /**
     * 상품 조회 이력 반환
     * 최신 순으로 조회해서 반환합니다.
     */
    public List<ProductHistoryResponse> getUserHistories(String userId) {
        return repository.findByUserId(userId, false)  // false는 최신순
                .stream()  // 스트림으로 변경한 뒤 엔티티 -> DTO로 변환한 리스트로 반환
                .map(ProductHistoryResponse::new)  // .map(ph -> new ProductHistoryResponse(ph))
                .collect(Collectors.toList());
    }
}