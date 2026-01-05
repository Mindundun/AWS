package com.example.awsNotifyPractice2.service;

import com.example.awsNotifyPractice2.component.EmailSender;
import com.example.awsNotifyPractice2.component.S3TemplateLoader;
import com.example.awsNotifyPractice2.dto.JoinMemberRequest;
import com.example.awsNotifyPractice2.dto.JoinMemberResponse;
import com.example.awsNotifyPractice2.entity.Member;
import com.example.awsNotifyPractice2.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotifyService {

    private final NotifyRepository notifyRepository;

    private final S3TemplateLoader s3TemplateLoader;
    private final EmailSender emailSender;

    // 이메일을 보내기 위함
    private final SesClient sesClient;

    // aws에서 인증 받은 이메일로 작성해야함.
    private final String SENDER = "pmk_1007@naver.com";

    public JoinMemberResponse join(JoinMemberRequest request) {
        // DB에 넣을 PK, 가입일시
        String memberId = UUID.randomUUID().toString().replace("-", "");
        String createdAt = LocalDateTime.now().toString();

        // DB에 넣을 엔티티
        Member member = Member.builder()
                .memberId(memberId)
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .createdAt(createdAt)
                .build();

        // DB에 저장
        notifyRepository.save(member);

        // S3 welcome.html 가져와 "{name}" 치환
        String html = s3TemplateLoader.loadWelcomeTemplate();
        html = html.replace("{name}", request.name());

        // 웰컴 메일 전송
        emailSender.sendEmail(request.email(), html, true);

        // 반환
        return new JoinMemberResponse(memberId, "멤버십 가입 성공!", createdAt);
    }
}
