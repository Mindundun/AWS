package com.example.awsSQSSecretsBasic.service;


import com.example.awsSQSSecretsBasic.dto.*;
import com.example.awsSQSSecretsBasic.entity.ShopData;
import com.example.awsSQSSecretsBasic.repository.ShopDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final SqsClient sqsClient;
    private final SecretService secretService;

    private final ShopDataRepository repository;
    private final ObjectMapper objectMapper; // JSON 메세지 컨버터


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

        //-------------------이메일 전송------------------
        // 1. 받는 사람
        Destination destination = Destination.builder()
                .toAddresses(request.email())
                .build();

        // 2. 제목
        Content subject = Content.builder()
                .data("주문 요청 : " + orderId)
                .build();

        // 3. 본문
        Content body = Content.builder()
                .data("주문 ID : " + orderId + "\n" + "주문총액 : " + orderInfo.getAmount())
                .build();

        // 4. 제목 + 본문 내용
        Message message = Message.builder()
                .subject(subject)
                .body(b -> b.text(body))
                .build();

        // 5. 이메일 전송 객체 생성
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(SENDER)
                .destination(destination)
                .message(message)
                .build();

        // 6. 이메일 전송
        sesClient.sendEmail(sendEmailRequest);

        // 주문 ID 반환
        return new CreateOrderResponse(
                orderId,
                orderInfo.getAmount(),  // 주문 총액
                "주문 완료! 이메일과 문자를 확인하세요!"
        );
    }

    // 비동기 전송
    public CreateOrderResponse createOrderEmailQueue(CreateOrderRequest request) {
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

        // SQS : Producer

        // 큐(SQS)에 저장할 메시지 만들기
        // 주문ID, 주문총액, 전화번호 정보 -> JSON 문자열
        String payload = null;
        try {
            MessageDto dto = new MessageDto(orderId, totalAmount, request.phone(),  request.email());

            // MessageDto To JSON String
            payload = objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String payload = null;
//                String.format("{\"orderId\": \"%s\", \"totalAmount\": %d, \"phone\": \"%s\"}", orderId, totalAmount, request.phone());

        // AWS Console에서 생성한 큐 URL
        String QUEUE_URL = secretService.getSecret("QUEUE_URL");


        // 큐(SQS)에 메세지 저장 (비동기로 동작)
        SendMessageRequest messageRequest = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(payload)   // JSON 문자열
                .build();

        sqsClient.sendMessage(messageRequest);

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
