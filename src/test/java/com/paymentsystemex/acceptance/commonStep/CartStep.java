package com.paymentsystemex.acceptance.commonStep;

import com.paymentsystemex.dto.cart.CartRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class CartStep {

    public static ExtractableResponse<Response> 장바구니_생성(String accessToken, Long productId, Long productOptionId, int quantity) {

        ExtractableResponse<Response> response =
                RestAssured.given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(new CartRequest(productId, productOptionId, quantity))
                        .when().post("/carts")
                        .then()
                        .extract();

        return response;
    }

    public static ExtractableResponse<Response> 내_장바구니_목록_전체조회(String accessToken) {

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/carts")
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 장바구니_수량_변경(String accessToken, Long cartId, int changeAmount) {

//        Map<String, Integer> params = new HashMap<>();
//        params.put("changeAmount", changeAmount);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .body(changeAmount)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/carts/" + cartId)
                .then()
                .extract();
    }
}
