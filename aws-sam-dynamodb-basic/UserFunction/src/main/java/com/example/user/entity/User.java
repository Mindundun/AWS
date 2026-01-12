package com.example.user.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

// 기본 생성자, Getter/Setter 필수

@DynamoDbBean  // DynamoDB 테이블과 매핑 관계의 클래스
public class User {
    private String userId;
    private String name;
    private int age;

    // 기본 생성자
    public User() {}

    public User(String userId, String name, int age) {
        this.userId = userId;
        this.name = name;
        this.age = age;
    }

    @DynamoDbPartitionKey  // Getter 이용 PK 설정
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}