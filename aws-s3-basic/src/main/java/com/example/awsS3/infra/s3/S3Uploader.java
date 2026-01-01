package com.example.awsS3.infra.s3;

import java.nio.file.Paths;
import java.util.UUID;

import com.example.awsS3.config.S3Config;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Uploader {
    // S3Client 가져오기
    static S3Client s3 = S3Config.getS3Client();

    public static void main(String[] args) {
        // uuid를 이용한 버킷 이름 생성
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String bucketName = "my-pmk-bucket-" + uuid;

        // key는 S3에 저장될 파일명
        String key = "uploads/hi.txt";

        // 로컬에 있는 파일 (업로드할 파일)
        String filePath = "/Users/mindun/IdeaProjects/AWS/hi.txt";  // C:/temp/hello.txt

        try {
            // [Step 1] 버킷 생성
            // 편의상 실행마다 새로운 버킷 생성
            System.out.println("Creating bucket: " + bucketName);
            s3.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build());

            // [Step 2] 파일 업로드
            System.out.println("Uploading file to S3 ...");
            PutObjectRequest putObject = PutObjectRequest.builder()
                    .bucket(bucketName)  // 목적지 버킷
                    .key(key)  // 저장될 파일명 (경로처럼 생각)
                    .contentType("text/plain")  // MIME 타입
                    .build();

            // PutObjectRequest(봉투) + RequestBody(내용물) 전송
            s3.putObject(putObject, RequestBody.fromFile(Paths.get(filePath)));

            System.out.println("Upload Complete!");
            System.out.println("URL: https://" + bucketName + ".s3.ap-northeast-2.amazoneaws.com/" + key);

        } catch (Exception e) {
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}