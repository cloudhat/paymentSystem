package com.paymentsystemex.domain.order.dto;

import core.domain.order.entity.Orders;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class OrderHistoryResponse {

    List<OrderResponse> orderResponseList;
    int totalPage;
    int totalElements;
    int pageNumber;

    public static OrderHistoryResponse fromEntity(Page<Orders> ordersList) {
        OrderHistoryResponse orderHistoryResponse = new OrderHistoryResponse();
        orderHistoryResponse.orderResponseList = ordersList.stream().map(OrderResponse::fromEntity).toList();

        orderHistoryResponse.totalPage = ordersList.getTotalPages();
        orderHistoryResponse.totalElements = (int) ordersList.getTotalElements();
        orderHistoryResponse.pageNumber = ordersList.getPageable().getPageNumber();

        return orderHistoryResponse;

    }
}
