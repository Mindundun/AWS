package com.example.lounge;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;


public class LoungeHandler implements RequestHandler <APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        // LoungeAuthorizerHandler의 반환 값(IAM Policy)에서 principalId 꺼내기
        Map<String, Object> authData = input.getRequestContext().getAuthorizer();
        String user = "Unknown";
        if (authData != null) {
            user = (String) authData.get("principalId");
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(user + ", Welcome to Membership Lounge!");
    }
}