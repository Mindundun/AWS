package com.example.awsNotifySES.dto;

public record EmailRequest(
        String to,      // 받는 사람
        String subject, // 제목
        String body     // 본문 내용
) {}
