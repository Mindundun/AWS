package com.example.awsNotifyPractice1.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SesClient sesClient;

    private final String SENDER_EMAIL = "pmk_1007@naver.com";  // 인증된 이메일

    // [이메일 발송]
    public void sendEmail(String to, String subject, String body) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(SENDER_EMAIL)
                    .destination(d -> d.toAddresses(to))
                    .message(msg -> msg
                            .subject(s -> s.data(subject))
                            .body(b -> b.text(t -> t.data(body)))) // 텍스트 모드
                    .build();

            sesClient.sendEmail(request);
            System.out.println("이메일 발송 성공: " + to);
        } catch (Exception e) {
            // 알림 실패가 주문을 막으면 안되니 로그만 남깁니다.
            System.err.println("이메일 발송 실패: " + e.getMessage());
        }
    }

}
