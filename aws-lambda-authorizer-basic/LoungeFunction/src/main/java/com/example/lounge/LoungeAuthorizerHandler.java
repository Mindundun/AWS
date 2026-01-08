package com.example.lounge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;

/**
 * Input 타입 APIGatewayCustomAuthorizerEvent
 *   - 토큰 검사 전용 이벤트 라이브러리.
 *
 * Output 타입 Map<String, Object>
 *   - IAM Policy 객체를 반환해야 하는데, 이는 JSON 객체임.
 *   - Map을 사용하면 라이브러리 의존성 없이 편하게 변환 가능.
 */
public class LoungeAuthorizerHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        String token = input.getAuthorizationToken();  // 전달된 토큰
        String methodArn = input.getMethodArn();  // 요청한 API 리소스 주소
        context.getLogger().log("Token received: " + token);

        // 토큰 검증 로직 (실무에서는 JWT 검증 등을 수행하지만, 여기서는 단순 문자열 비교)
        if ("GOLD-MEMBER".equals(token)) {
            // Allow
            return generateIamPolicy("gold-member", "Allow", methodArn);
        } else {
            // Deny
            return generateIamPolicy("gold-member", "Deny", methodArn);
        }
    }

    // IAM Policy JSON 생성
    private Map<String, Object> generateIamPolicy(String principalId, String effect, String resource) {
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