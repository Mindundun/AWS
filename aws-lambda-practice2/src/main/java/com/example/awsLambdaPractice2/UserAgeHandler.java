package com.example.awsLambdaPractice2;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.example.awsLambdaPractice2.dto.UserInfoRequest;
import com.example.awsLambdaPractice2.dto.UserInfoResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.time.Year;

public class UserAgeHandler implements RequestHandler<UserInfoRequest, UserInfoResponse> {
    @Override
    public UserInfoResponse handleRequest(UserInfoRequest request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(String.format("Received request: %s", request));

        // 환경 변수 설정
        String GENDER = System.getenv("GENDER");

        if (GENDER == null) {
            GENDER = "Female";
        }

        // 현재 연도
        int currentYear = Year.now().getValue();

        // 입력받은 생일
        int birthDateYear = Integer.parseInt(request.birthday().substring(0, 4));

        // 나이 계산
        int age = currentYear - birthDateYear;

        if ( 0 > age && age < 121 ) {
            throw new IllegalArgumentException("나이가 0 초과 120 미만입니다.");
        }

        String status = ( age >= 20 ) ? "adult":"child";

        return new UserInfoResponse(
                request.name(),
                age,
                GENDER,
                status
        );

    }
}
