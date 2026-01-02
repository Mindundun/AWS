package com.example.awsS3Example2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.awsS3Example2.dto.S3FileDto;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final String bucket = "my-bucket-pmk";

    // 파일 목록 반환
    public List<S3FileDto> getFileList() {
        // 객체 리스트 요청 객체
        ListObjectsV2Request v2Request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();

        // 객체 리스트 요청 (실제 요청은 이 곳)
        ListObjectsV2Response v2Response = s3Client.listObjectsV2(v2Request);

        // 가져온 객체는 S3Object 타입 -> List에 S3FileDto로 변환해서 담아주기
        List<S3FileDto> files = new ArrayList<>();

        // 이런 진행의 코드는 Java Lambda Stream 방식이 사실 더 좋습니다.
        for (S3Object content : v2Response.contents()) {
            S3FileDto dto = S3FileDto.builder()
                    .key(content.key())
                    .size(content.size())
                    .lastModified(content.lastModified().toString())
                    .build();
            files.add(dto);
        }

        return files;

    }

    // 다운로드
    public ResponseBytes<GetObjectResponse> download(String key) {
        // S3 객체 가져올 때 사용하는 GetObjectRequest
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // 실제로 가져와서 반환
        // toBytes(): 가져온 객체를 메모리에 로드
        // 서버의 메모리를 사용하는 방식이므로 대용량 파일 주의해야 함!
        // 다음에는 toInputString()으로 스트림만 연결해 주는 방식 사용할 예정
        return s3Client.getObject(objectRequest, ResponseTransformer.toBytes());
    }

}