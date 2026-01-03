package com.example.awsS3PresignerUpload.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final String bucketName = "my-bucket-pmk";

    // Presigned URL 생성 메서드
    public String getPreSignedUrl(String originalFilename) {
        String filename= UUID.randomUUID() + "_" + originalFilename;

        // 1. 실제 업로드할 때 사용할 요청 정보 미리 정의
        // 주의: 여기서 정의한 contentType과 나중에 클라이언트가 올릴 때 헤더가 일치해야 합니다.
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType("image/png")  // 예시로 png 고정 (실제로는 파라미터로 받음)
                .build();

        // 2. 프리사인 요청 생성 (유효기간 설정)
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // 5분 뒤 만료
                .putObjectRequest(objectRequest)
                .build();

        // 3. URL 발급
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        // 4. URL 문자열 반환
        return presignedRequest.url().toString();
    }
}