package com.paymentsystemex.acceptance.commonStep;

import com.paymentsystemex.dto.cart.CartResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartStep {

    public static ExtractableResponse<Response> 장바구니_생성(Long productId, Long productOptionId, int quantity) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("productOptionId", productOptionId);
        params.put("quantity", quantity);

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/carts")
                        .then().log().all()
                        .extract();

        return response;
    }

    public static List<CartResponse> 내_장바구니_목록_전체조회() {

        List<CartResponse> cartResponseList =
                RestAssured.given()
                        .when().get("/carts")
                        .then()
                        .extract().jsonPath().getList("cartResponse");

        return cartResponseList;
    }
}
