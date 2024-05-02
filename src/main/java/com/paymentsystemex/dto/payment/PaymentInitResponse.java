package com.paymentsystemex.dto.payment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaymentInitResponse {

    private Long paymentId;
    private String productName;
    private int totalPayAmount;
    private String customerName;

}
