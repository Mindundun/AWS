package com.example.awsS3Example2.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.awsS3Example2.dto.S3FileDto;
import com.example.awsS3Example2.service.S3Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Controller
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/")
    public String index(Model model) {
        List<S3FileDto> files = s3Service.getFileList();  // 서비스로부터 반환받은 값
        model.addAttribute("files", files);  // 화면으로 files 전달
        return "index";  // templates/index.html 파일 이동
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@RequestParam("key") String key) {
        try {
            // S3 객체 가져오기
            ResponseBytes<GetObjectResponse> bytes = s3Service.download(key);

            // 원래 파일명 (추가로 인코딩 필요할 수 있음)
            String originalFilename = key.substring(key.indexOf("_") + 1);

            // 다운로드용 HTTP 헤더 설정 (org.springframework.http.HttpHeaders)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", originalFilename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // "application/octet-stream"

            // 응답 (다운로드)
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new ByteArrayResource(bytes.asByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();  // 서버 코드 오류가 없다면 S3 객체가 없을 것으로 판단
        }
    }

}
