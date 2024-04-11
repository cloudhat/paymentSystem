package com.paymentsystemex.acceptance.commonStep;

import com.paymentsystemex.dto.payment.PaymentRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PaymentStep {

    public static ExtractableResponse<Response> 결제_생성(String accessToken, PaymentRequest paymentRequest) {
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(paymentRequest)
                        .when().post("/payments/init")
                        .then().log().all()
                        .extract();

        return response;
    }
}
