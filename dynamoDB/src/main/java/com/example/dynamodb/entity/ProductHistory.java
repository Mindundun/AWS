package com.example.dynamodb.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean  // DynamoDB 테이블과 매핑
public class ProductHistory {

    private String userId;
    private String viewTime;
    private String productId;
    private String productName;
    private Long price;

    @DynamoDbPartitionKey  // Partition Key (PK)
    public String getUserId() {
        return userId;
    }

    @DynamoDbSortKey  // Sort Key (SK)
    public String getViewTime() {
        return viewTime;
    }

    // Getter
    public String getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public Long getPrice() {
        return price;
    }
}
