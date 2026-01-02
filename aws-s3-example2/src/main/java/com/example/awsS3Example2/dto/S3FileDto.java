package com.example.awsS3Example2.dto;

import lombok.Builder;

// record
// 모든 필드는 자동으로 private final 처리
// @NoArgsConstructor 미지원
// @AllArgsConstructor 지원
// @Setter 미지원
// @Getter 지원 (get 필요 없음)

@Builder  // 지원! 야호!
public record S3FileDto(
        String key,
        Long size,
        String lastModified
) {
    // public S3FileDto(String key, long size) {
    //   this(key, size, "디폴트");
    // }
}