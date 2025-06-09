package com.paymentsystemex.acceptance.commonStep;

import core.domain.payment.dto.PaymentRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PaymentStep {

    public static ExtractableResponse<Response> 결제_생성(String accessToken, PaymentRequest paymentRequest) {

        return  RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .body(paymentRequest)
                .when().post("/payments/init")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 결제_진행(String accessToken, Long paymentId, String payKey) {

        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .when().post("/payments/transaction?paymentId=" + paymentId + "&payKey=" + payKey)
                .then()
                .extract();
    }
}
