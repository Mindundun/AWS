package com.example.awsLambdaPractice2.dto;

public record UserInfoResponse (
        String name,
        int age,
        String gender,
        String status
){}
