package com.example.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class OrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    // Mock Data
    private final List<Map<Integer, String>> orders = new ArrayList<>();

    public OrderHandler() {
        orders.add(Map.of(100, "MacBook Pro"));
        orders.add(Map.of(200, "Apple Watch"));
        orders.add(Map.of(300, "iPad Air"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        // Path Parameter
        Map<String, String> pathParams = input.getPathParameters();
        String strOrderId = pathParams != null ? pathParams.get("orderId") : null;
        context.getLogger().log("OrderId: " + strOrderId);

        // PrincipalId
        Map<String, Object> authData = input.getRequestContext().getAuthorizer();
        String user = "Unknown";
        if (authData != null) {
            user = (String) authData.get("principalId");
        }
        context.getLogger().log("PrincipalId: " + user);

        // 비즈니스 로직
        if (strOrderId != null) {
            int orderId = Integer.parseInt(strOrderId);
            for (Map<Integer, String> order : orders) {
                if (order.containsKey(orderId)) {
                    String item = order.get(orderId);
                    String body = String.format("{\"orderId\": %d, \"item\": %s, \"user\": %s}", orderId, item, user);
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withBody(body);
                }
            }
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(404)
                .withBody("{\"message\": \"Order not found\"}");  // {"message": "Order not found"}
    }
}
