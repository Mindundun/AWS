package com.example.awsSQSSecretsBasic.component;

import com.example.awsSQSSecretsBasic.dto.MessageDto;
import com.example.awsSQSSecretsBasic.service.NotificationService;
import com.example.awsSQSSecretsBasic.service.SecretService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.text.DecimalFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationWorker {

    private final SqsClient sqsClient;
    private final SecretService secretService;

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    // fixedDelay : Queue 읽은 후 10초 쉬기
    // fixedRate : Queue 읽기 시작한 뒤 10초마다~ 만약 읽는데 2초 걸렸으면 8초 쉼
    @Scheduled(fixedDelay = 10000) // 1000 = 1초
    public void pollQueue() {
        String QUEUE_URL = secretService.getSecret("QUEUE_URL");

        // 큐 메시지 수신 객체 생성
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(10) // 한번에 최대 10개 메시지 읽기
                .waitTimeSeconds(20) // 대기시간 20초
                .build();

        // 큐 메시지 수신
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        // 메세지 순회하며 처리
        for (Message message : messages) {
            try {
                MessageDto dto = objectMapper.readValue(message.body(), MessageDto.class);

                // 내용 추출
                String orderId = dto.orderId();
                Long totalAmount = dto.totalAmount();
                String phone = dto.phone();
                String email = dto.email();

                // 이메일 보내기
                notificationService.sendEmail(
                        email,
                        "[서비스명] 주문이 완료되었습니다.",
                        "주문 ID: " + orderId + "\n주문 총액: " + new DecimalFormat("#,##0").format(totalAmount));

                // 큐 메시지 삭제 객체 생성
                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .receiptHandle(message.receiptHandle())
                        .build();

                // 큐 메세지 삭제
                sqsClient.deleteMessage(deleteRequest);

                System.out.println("SQS 메시지 처리 완료 : " + orderId);
            } catch (Exception e) {
                System.err.println("SQS 메시지 처리 실패 : " +e.getMessage());
                // 실패 시 자동으로 재시동

            }

        }
    }
}
