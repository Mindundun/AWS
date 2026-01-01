package com.example.awsS3.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Config {
    // S3 클라이언트는 Thread-Safe 하므로 하나만 만들어 재사용하는 것이 원칙입니다.
    private static final S3Client s3Client = S3Client.builder()
            .region(Region.AP_NORTHEAST_2) // 서울 리전 필수. 이미 AWS가 환경 변수로 등록해 둔 상태: region(Region.of(System.getenv("AWS_REGION")))
            .build();

    public static S3Client getS3Client() {
        return s3Client;
    }
}