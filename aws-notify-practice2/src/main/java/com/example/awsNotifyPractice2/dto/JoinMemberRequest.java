package com.example.awsNotifyPractice2.dto;

public record JoinMemberRequest(
        String name,
        String email,
        String phone
) {}