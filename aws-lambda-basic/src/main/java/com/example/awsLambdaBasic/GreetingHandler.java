package com.example.awsLambdaBasic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class GreetingHandler implements RequestHandler<Map<String, String>, String> {
    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        // Context를 통한 로거 활용 (로그 기록은 aws 내 cloud watch에서 확인 가능)
        context.getLogger().log("Input: " + input);

        // 환경 변수 (AWS Console에서 직접 세팅)
        String greetingPrefix = System.getenv("GREETING_PREFIX");

        if (greetingPrefix == null) {
           greetingPrefix = "Hello";
        }

        // 입력 값 처리
        String name = input.getOrDefault("name", "Guest");

        // 비즈니스 로직
        String result = String.format("%s, %s님", greetingPrefix, name);

        // Lambda의 실행 결과 반환
        return result;

    }
}
