package com.example.awsLambdaPractice1;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.awsLambdaPractice1.dto.CalcRequest;

import java.util.Map;

public class CalculatorHandler implements RequestHandler<Map<String, String>, String> {
    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        // 현재 Jackson이 없어 아래처럼 직접 DTO에 값 넣어주기
        CalcRequest calcRequest = new CalcRequest();
        calcRequest.setNum1(Double.parseDouble(input.get("num1")));
        calcRequest.setNum2(Double.parseDouble(input.get("num2")));
        calcRequest.setOp(input.get("op"));

        double num1 = Double.parseDouble(input.get("num1"));
        double num2 = Double.parseDouble(input.get("num2"));
        String op = input.get("op");

        // Context를 통한 로거 활용 (로그 기록은 aws 내 cloud watch에서 확인 가능)
        context.getLogger().log("계산 시작: " + calcRequest.getNum1() + " " + calcRequest.getOp() + " " + calcRequest.getNum2());

        // 환경 변수
        String calculatorName = System.getenv("CALCULATOR_NAME");

        if (calculatorName == null) {
            calculatorName = "Mindundun's youngMin cal!!";
        }

        // 입력 값 처리
        Double result = 0.0;
        switch (calcRequest.getOp()) {
            case "+" -> result = calcRequest.getNum1() + calcRequest.getNum2();
            case "-" -> result = calcRequest.getNum1() - calcRequest.getNum2();
            case "*" -> result = calcRequest.getNum1() * calcRequest.getNum2();
            case "/" -> {
                if (calcRequest.getNum2() == 0) {
                    throw new IllegalArgumentException("0으로 나눈다!!");
                }
                result = calcRequest.getNum1() / calcRequest.getNum2();
            }
            default -> throw new IllegalArgumentException("Invalid operation!");
        }

        String resultStr = String.format("[%s] %.1f %s %.1f = %.2f",
                calculatorName,
                calcRequest.getNum1(),
                calcRequest.getOp(),  // 여기 %가 들어가면 안됨
                calcRequest.getNum2(),
                result);

        return resultStr;


    }

}
