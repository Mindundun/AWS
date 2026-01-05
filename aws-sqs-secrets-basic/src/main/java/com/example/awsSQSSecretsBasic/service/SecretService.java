package com.example.awsSQSSecretsBasic.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@RequiredArgsConstructor
@Service
public class SecretService {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper;

    // 가져온 Secret을 캐싱해 둘 Map
    private Map<String, String> secretCache;

    @PostConstruct
    public void init() {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId("OrderApp/Prod/Config-pmk")  // AWS Console에서 만든 보안 암호 이름
                    .build();

            GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
            String secretJson = response.secretString();  // JSON 문자열 {"QUEUE_URL": "https://...", "SENDER_EMAIL": "admin@..."}
            // secretCache = objectMapper.readValue(secretJson, Map.class);  타입 체크 없음
            secretCache = objectMapper.readValue(secretJson, new TypeReference<Map<String, String>>() {});  // 타입 체크 있음

            System.out.println("보안 설정 로드 성공");
        } catch (Exception e) {
            throw new RuntimeException("보안 설정 로드 실패", e);
        }
    }

    public String getSecret(String key) {
        return secretCache.get(key);
    }
}
