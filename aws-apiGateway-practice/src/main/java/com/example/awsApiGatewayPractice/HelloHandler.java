package com.example.awsApiGatewayPractice;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDateTime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HelloHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    // ObjectMapper는 생성 비용이 비싸므로 static으로 만들어 재사용하는 것이 좋습니다.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // 1. Query String 파라미터 가져오기
        Map<String, String> queryParams = input.getQueryStringParameters();

        String name = "";
        String city = "";

        if (queryParams != null) {
            // null 체크와 함께 값 가져오기
            name = queryParams.getOrDefault("name", "Guest");
            city = queryParams.getOrDefault("city", "Korea");
        }

        // 2. 환경 변수
        String nation = System.getenv("NATION");
        if (nation == null) {
            nation = "Korea";
        }

        // 3. 응답 데이터를 담을 Map 생성 (또는 DTO를 만들어 사용해도 됩니다)
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", String.format("Hello, %s! Welcome to %s, %s.", name, city, nation));
        responseBody.put("name", name);
        responseBody.put("city", city);
        responseBody.put("timestamp", LocalDateTime.now().toString());

        try {
            // 4. 자바 객체(Map) -> JSON 문자열 변환
            String jsonOutput = objectMapper.writeValueAsString(responseBody);

            // 5. 응답 헤더 설정
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");  // 응답 body(jsonOutput)가 JSON임을 명시
            headers.put("X-Custom-Header", "custom value"); // 커스텀 헤더

            // 6. 최종 응답 생성
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(headers)
                    .withBody(jsonOutput);

        } catch (Exception e) {
            context.getLogger().log("JSON Error: " + e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Internal Server Error");
        }
    }
}
