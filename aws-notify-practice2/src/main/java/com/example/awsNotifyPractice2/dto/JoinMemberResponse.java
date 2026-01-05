package com.example.awsNotifyPractice2.dto;

public record JoinMemberResponse(
        String memberId,
        String message,
        String createdAt
) {}