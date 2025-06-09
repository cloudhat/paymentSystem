package core.domain.order.dto;

import core.domain.order.entity.payment.PaymentMethod;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PaymentRequest {

    private final List<Long> couponIdList = new ArrayList<>();
    private final Long addressId;
    private String idempotencyKey;
    private PaymentMethod paymentMethod;
    private String eventType;

    public PaymentRequest(String idempotencyKey, PaymentMethod paymentMethod, String eventType, List<Long> couponIdList, Long addressId) {
        this.idempotencyKey = idempotencyKey;
        this.paymentMethod = paymentMethod;
        this.eventType = eventType;
        this.couponIdList.addAll(couponIdList);
        this.addressId = addressId;

    }
}
