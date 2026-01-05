package com.example.awsNotifyPractice2.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Builder
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean  // DynamoDB 테이블과 매핑
public class Member {
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private String createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("memberId")
    public String getMemberId() { return memberId; }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getCreatedAt() {
        return createdAt;
    }
}
