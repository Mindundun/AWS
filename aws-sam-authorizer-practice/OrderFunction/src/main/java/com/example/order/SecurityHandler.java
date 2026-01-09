package com.example.order;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;

public class SecurityHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {

        String token = input.getAuthorizationToken();  // 전달된 토큰
        String methodArn = input.getMethodArn();  // 요청한 API 리소스 주소

        // 토큰 검사 및 PrincipalId 부여
        if (token.equals("my-secret-key")) {
            return generatePolicy("user-001", "Allow", methodArn);
        } else {
            return generatePolicy("anonymous", "Deny", methodArn);
        }
    }

    // IAM Policy JSON 생성
    private Map<String, Object> generatePolicy(String principalId, String effect, String resource) {
        // principalId
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("principalId", principalId);

        // policyDocument (IAM Policy)
        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put("Version", "2012-10-17");
        Map<String, Object> statement = new HashMap<>();
        statement.put("Action", "execute-api:Invoke");
        statement.put("Effect", effect);
        statement.put("Resource", resource);
        policyDocument.put("Statement", Collections.singletonList(statement));
        authResponse.put("policyDocument", policyDocument);

        return authResponse;
    }
}
