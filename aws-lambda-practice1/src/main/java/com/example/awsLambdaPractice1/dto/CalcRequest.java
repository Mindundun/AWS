package com.example.awsLambdaPractice1.dto;

import lombok.Getter;

@Getter
public class CalcRequest {
    private double num1;
    private double num2;

    private String op;

    public void setNum1(double num1) { this.num1 = num1; }
    public void setNum2(double num2) { this.num2 = num2; }

    public void setOp(String op) { this.op = op; }
}
