package com.paymentsystemex.dto.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class OrderRequest {
    private final List<Long> cartIdList = new ArrayList<>();

    public OrderRequest(List<Long> cartIdList) {
        this.cartIdList.addAll(cartIdList);
    }
}
