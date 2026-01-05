package com.example.awsNotifyPractice1.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

// 주문(ORDER) + 주문 상품(ITEM) 결합된 엔티티
@Builder
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean  // DynamoDB 테이블과 매핑
public class ShopData {

    private String pk;  // 주문번호
    private String sk;  // "INFO" 또는 "ITEM#" (데이터 구분 용도)
    private String type;  // "ORDER" 또는 "ITEM" (구분자)
    private String info;  // "ORDER"인 경우 주문상태(ORDER_CREATED 등), "ITEM"인 경우 상품명(PIZZA, COKE)
    private Long amount;  // "ORDER"인 경우 주문총액(20000), "ITEM"인 경우 상품가격(15000, 5000)

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")  // 실제 DB 칼럼명은 PK
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")  // 실제 DB 칼럼명은 SK
    public String getSk() {
        return sk;
    }

    // 그 외 Getter
    public String getType() {
        return type;
    }
    public String getInfo() {
        return info;
    }
    public Long getAmount() {
        return amount;
    }
}