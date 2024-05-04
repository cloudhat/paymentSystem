package com.paymentsystemex.dto.order;

import com.paymentsystemex.domain.order.OrderPriceHistory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderPriceHistoryResponse {
    String priceType;
    String reason;
    int amount;
    LocalDateTime createDate;

    public static OrderPriceHistoryResponse fromEntity(OrderPriceHistory orderPriceHistory){
        OrderPriceHistoryResponse orderPriceHistoryResponse = new OrderPriceHistoryResponse();
        orderPriceHistoryResponse.priceType = orderPriceHistory.getPriceType().toString();
        orderPriceHistoryResponse.reason = orderPriceHistory.getReason();
        orderPriceHistoryResponse.amount = orderPriceHistory.getAmount();
        orderPriceHistoryResponse.createDate = orderPriceHistory.getCreateDate();

        return orderPriceHistoryResponse;
    }
}
