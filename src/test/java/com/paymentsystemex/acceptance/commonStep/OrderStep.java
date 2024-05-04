package com.paymentsystemex.acceptance.commonStep;

import com.paymentsystemex.dto.order.OrderHistoryRequest;
import com.paymentsystemex.dto.order.OrderRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.List;

public class OrderStep {

    public static ExtractableResponse<Response> 주문_생성(String accessToken, List<Long> cartIdList) {

        ExtractableResponse<Response> response =
                RestAssured.given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
                        .body(new OrderRequest(cartIdList))
                        .when().post("/orders")
                        .then()
                        .extract();

        return response;
    }

    public static ExtractableResponse<Response> 내_주문정보_조회(String accessToken, String idempotencyKey) {

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/orders/checkout/" + idempotencyKey)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_주문내역_목록_조회(String accessToken, OrderHistoryRequest orderHistoryRequest) {

        String url = "/orders?1=1";
        if (orderHistoryRequest.getCreateAtStartDate() != null) {
            url += "&createAtStartDate=" + orderHistoryRequest.getCreateAtStartDate();
        }
        if (orderHistoryRequest.getCreateAtEndDate() != null) {
            url += "&createAtEndDate=" + orderHistoryRequest.getCreateAtEndDate();
        }
        if (orderHistoryRequest.getPageNum() != 0) {
            url += "&pageNum=" + orderHistoryRequest.getPageNum();
        }
        if (!orderHistoryRequest.getDescending()) {
            url += "&descending=false";
        }

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get(url)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 내_주문내역_상세_조회(String accessToken, Long orderId) {

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/orders/" + orderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_취소(String accessToken, Long orderId) {

        ExtractableResponse<Response> response =
                RestAssured.given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken)
                        .when().patch("/orders/cancel/"+orderId)
                        .then()
                        .extract();

        return response;
    }
}
