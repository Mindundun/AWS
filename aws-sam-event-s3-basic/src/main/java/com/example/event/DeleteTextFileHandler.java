package com.example.event;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

/**
 * 입력 타입: S3Event
 * "s3:ObjectRemoved:" 이벤트 발생하면 동작하는 Lambda 함수
 * 주의: 파일이 이미 삭제되었으므로 내용을 읽을(getObject) 수 없습니다.
 */
public class DeleteTextFileHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event input, Context context) {
        // 이벤트 루프
        for (S3EventNotificationRecord record : input.getRecords()) {
            // 삭제된 파일의 정보 추출
            S3Entity entity = record.getS3();
            String bucketName = entity.getBucket().getName();
            String key = entity.getObject().getUrlDecodedKey();  // 디코딩 된 키 필요

            // [중요] 여기서는 s3Client.getObject()를 호출하면 안 된다.
            // 이미 삭제된 파일이라 404 NoSuchKey 에러가 발생하기 때문이다.

            // 로그 남기기: 누가 무엇을 지웠는지 기록 (Audit Log 역할)
            String message = String.format("[ALERT] File Deleted! Bucket: %s, File: %s", bucketName, key);
            context.getLogger().log(message);
        }
        return "DeleteTextFileHandler Processed";
    }
}
