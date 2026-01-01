package com.example.awsS3Example1.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "my-bucket-pmk";

    public String uploadFile(MultipartFile file) throws IOException{
        // 매일 새로운 폴더를 생성하기 위함
        String key = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + file.getOriginalFilename();

        // 브라우저에서 바로 볼 수 있도록 메타 데이터 설정
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        // 업로드 실행
        s3Client.putObject(
                objectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        // 접근 가능한 URL반환
        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
    }

}
