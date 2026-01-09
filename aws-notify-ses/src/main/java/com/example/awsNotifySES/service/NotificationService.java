package com.example.awsNotifySES.service;

import com.example.awsNotifySES.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SesClient sesClient;

    // aws에서 인증 받은 이메일로 작성해야함.
    private final String SENDER = "pmk_1007@naver.com";

    /**
     *  이메일 보내기
     */
    public String sendEmail(EmailRequest emailRequest) {
        // 1. 받는 사람
        Destination destination = Destination.builder()
                .toAddresses(emailRequest.to())
                .build();

        // 2. 제목
        Content subject = Content.builder()
                .data(emailRequest.subject())
                .build();

        // 3. 본문
        Content body = Content.builder()
                .data(emailRequest.body())
                .build();

        // 4. 제목 + 본문 내용
        Message message = Message.builder()
                .subject(subject)
                .body(b -> b.text(body))
                .build();

        // 5. 이메일 전송 객체 생성
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(SENDER)
                .destination(destination)
                .message(message)
                .build();

        // 6. 이메일 전송
        sesClient.sendEmail(sendEmailRequest);

        // 7. 반환
        return "Email send successfully : " + emailRequest.to();

    }

}
