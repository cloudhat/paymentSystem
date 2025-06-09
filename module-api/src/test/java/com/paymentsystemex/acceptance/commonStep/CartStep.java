package com.paymentsystemex.acceptance.commonStep;

import core.domain.cart.dto.CartRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class CartStep {

    public static ExtractableResponse<Response> 장바구니_생성(String accessToken, Long productId, Long productOptionId, int quantity) {

        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .body(new CartRequest(productId, productOptionId, quantity))
                .when().post("/carts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_장바구니_목록_전체조회(String accessToken) {

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/carts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 장바구니_수량_변경(String accessToken, Long cartId, int changeAmount) {

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .body(changeAmount)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/carts/" + cartId)
                .then()
                .extract();
    }
}
