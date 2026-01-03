package com.example.awsS3PresignerUpload.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.awsS3PresignerUpload.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PresignedUrlController {

    private final S3Service s3Service;

    // GET /presigned-url?filename=test.png
    @GetMapping("/presigned-url")
    public String getUrl(@RequestParam String filename) {
        return s3Service.getPreSignedUrl(filename);
    }

    @GetMapping("/download-url")
    public String getPreviewUrl(@RequestParam String key) {
        return s3Service.getPresignedDownloadUrl(key);
    }
}