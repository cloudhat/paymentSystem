package com.paymentsystemex.domain.payment.dto;

import com.paymentsystemex.domain.payment.entity.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaymentTransactionResponse {
    private String reason;
    private LocalDateTime expireDate;
    private PaymentStatus paymentStatus;
}
