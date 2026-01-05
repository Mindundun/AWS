package com.example.awsNotifyPractice1.service;


import com.example.awsNotifyPractice1.dto.CreateOrderRequest;
import com.example.awsNotifyPractice1.dto.CreateOrderResponse;
import com.example.awsNotifyPractice1.dto.Item;
import com.example.awsNotifyPractice1.dto.OrderResponse;
import com.example.awsNotifyPractice1.entity.ShopData;
import com.example.awsNotifyPractice1.repository.ShopDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ShopDataRepository repository;
    private final NotificationService notificationService;

    private static final String ITEM_PREFIX = "ITEM#";

    // 이메일을 보내기 위함
    private final SesClient sesClient;

    // aws에서 인증 받은 이메일로 작성해야함.
    private final String SENDER = "pmk_1007@naver.com";


    public CreateOrderResponse createOrder(List<Item> items) {
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
        return new CreateOrderResponse(
                orderId,
                orderInfo.getAmount(),  // 주문 총액
                "주문 완료! 이메일과 문자를 확인하세요!"
        );
    }

    public CreateOrderResponse createOrderEmail(CreateOrderRequest request) {
        // 주문 ID 생성
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 4);  // 첫 4글자만 사용
        String orderId = "ORD_" + timestamp + "_" + uuid;

        List<Item> items = request.items();

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

        String emailBody = String.format("고객님, 주문해주셔서 감사합니다.\n주문번호: %s\n결제금액: %d원", orderId, totalAmount);

        // 이메일 발송 - 지금은 동기 방식 (비동기로 변경 예정)
        notificationService.sendEmail(request.email(), "주문해 주셔서 감사합니다!", emailBody);


        // 주문 ID 반환
        return new CreateOrderResponse(
                orderId,
                orderInfo.getAmount(),  // 주문 총액
                "주문 완료! 이메일과 문자를 확인하세요!"
        );
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
