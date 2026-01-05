package com.example.awsNotifyPractice2.controller;

import com.example.awsNotifyPractice2.dto.JoinMemberRequest;
import com.example.awsNotifyPractice2.dto.JoinMemberResponse;
import com.example.awsNotifyPractice2.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class NotifyController {

    private final NotifyService notifyService;

    @PostMapping
    public ResponseEntity<JoinMemberResponse> createOrder(@RequestBody JoinMemberRequest request) {

        JoinMemberResponse response = notifyService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
