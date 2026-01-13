package com.example.event;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 * 입력 타입: S3Event
 * "s3:ObjectCreated:*" 이벤트 발생하면 동작하는 Lambda 함수
 */
public class CreateTextFileHandler implements RequestHandler<S3Event, String> {

    private final S3Client s3Client = S3Client.create();

    @Override
    public String handleRequest(S3Event input, Context context) {
        // 이벤트 루프: 한 번에 여러 파일이 들어 올 수 있으므로
        for (S3EventNotificationRecord record : input.getRecords()) {
            // bucketName + key
            S3Entity entity = record.getS3();
            String bucketName = entity.getBucket().getName();
            String key = entity.getObject().getUrlDecodedKey();  // 디코딩 된 키 필요

            // 로그 남기기: 파일명
            context.getLogger().log("New Text File Detected: " + key);

            // S3에 업로드 된 파일 읽기
            try {
                // S3 파일을 바이트 배열로 받음
                ResponseBytes<GetObjectResponse> s3Object = s3Client.getObject(
                        GetObjectRequest.builder().bucket(bucketName).key(key).build(),
                        ResponseTransformer.toBytes()
                );
                // UTF-8 처리
                String content = s3Object.asUtf8String();
                // 로그 남기기: 파일 내용
                context.getLogger().log("File Content Preview: " +
                        content.substring(0, Math.min(content.length(), 100)));
            } catch (Exception e) {
                context.getLogger().log("Error reading file: " + e.getMessage());
            }
        }
        return "CreateTextFileHandler Processed";
    }
}
