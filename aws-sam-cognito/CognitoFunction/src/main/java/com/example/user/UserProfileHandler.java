package com.example.user;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;

public class UserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        // API Gateway의 Authorizer가 넘겨준 사용자 정보(Claims) 가져오기
        Map<String, Object> authorizer = input.getRequestContext().getAuthorizer();
        Map<String, Object> claims = (Map<String, Object>) authorizer.get("claims");

        // Claims 안에서 정보 꺼내기
        String email = (String) claims.get("email");
        String sub = (String) claims.get("sub");  // 유저 고유 UUID
        String username = (String) claims.get("cognito:username");

        // 로그
        context.getLogger().log("User Email: " + email);

        // 응답 생성
        String outputBody = String.format("{\"message\": \"Hello, %s! AWS validated your token.\", \"email\": \"%s\", \"sub\": \"%s\"}", username, email, sub);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(
                        Map.of(
                                "Content-Type", "application/json",
                                "Access-Control-Allow-Origin", "*" // CORS 헤더 (필수)
                        )
                )
                .withBody(outputBody);
    }
}