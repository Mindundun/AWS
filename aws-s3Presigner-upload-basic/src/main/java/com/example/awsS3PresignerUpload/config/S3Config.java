package com.example.awsS3PresignerUpload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    // Presigned URL 발급 전용
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

}