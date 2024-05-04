package com.paymentsystemex.service.payment.paymentInitStrategy;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.member.address.Address;
import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.order.PriceType;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.payment.PaymentMethod;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.repository.CartRepository;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.DeliveryFeePolicy;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.PricePolicy;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.ProductPricePolicy;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NaverPayPaymentInitStrategy extends PaymentInitStrategy {

    private final MemberRepository memberRepository;

    public NaverPayPaymentInitStrategy(CartRepository cartRepository, MemberRepository memberRepository) {
        super(cartRepository);
        this.memberRepository = memberRepository;
    }

    @Override
    String getEventType() {
        return "NAVER_PAY";
    }

    @Override
    @Transactional
    protected Payment initPayment(PaymentRequest paymentRequest, Member member, Orders orders) {

        //결제수단이 네이버페이가 아닐 경우 예외 처리
        if(!paymentRequest.getPaymentMethod().equals(PaymentMethod.NAVER_PAY)){
            throw new IllegalArgumentException("Payment method is not NAVER_PAY");
        }

        List<OrderProduct> orderProducts = orders.getOrderProducts();
        Address address = memberRepository.findAddressById(paymentRequest.getAddressId(), member.getId()).orElseThrow(EntityNotFoundException::new);

        PricePolicy pricePolicy = new ProductPricePolicy(orderProducts)
                .setNextPricePolicy(new NaverPayPolicy())
                .setNextPricePolicy(new DeliveryFeePolicy(address));

        List<OrderPriceHistory> orderPriceHistoryList = pricePolicy.getOrderPriceList(0, orders);

        int totalPayAmount = orderPriceHistoryList.stream()
                .mapToInt(OrderPriceHistory::getAmount).sum();

        return new Payment(totalPayAmount, 0, paymentRequest.getPaymentMethod(), paymentRequest.getEventType(), orders, member);
    }

    @Override
    protected OrderPriceHistory cancelPayment(Orders orders) {
        Payment payment = orders.getNormalPayment();

        payment.changeStatusToCancel();

        return new OrderPriceHistory(orders, PriceType.CANCEL, "결제취소", payment.getTotalPayAmount());
    }

}
