package com.paymentsystemex.acceptance.commonStep;

import com.paymentsystemex.dto.cart.CartRequest;
import com.paymentsystemex.dto.order.OrderRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStep {

    public static ExtractableResponse<Response> 주문_생성(String accessToken, List<Long> cartIdList) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(new OrderRequest(cartIdList))
                        .when().post("/orders")
                        .then().log().all()
                        .extract();

        return response;
    }

    public static ExtractableResponse<Response> 내_주문_조회(String accessToken,String idempotencyKey) {

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/orders/" + idempotencyKey)
                .then()
                .extract();
    }
}
