package com.paymentsystemex.service.payment;

import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.dto.payment.PaymentResponse;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.OrderRepository;
import com.paymentsystemex.service.ProductService;
import com.paymentsystemex.service.payment.paymentInitStrategy.PaymentInitStrategy;
import com.paymentsystemex.service.payment.paymentInitStrategy.PaymentInitStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final PaymentInitStrategyFactory paymentInitStrategyFactory;
    private final OrderRepository orderRepository;


    @Transactional
    public PaymentResponse initPayment(PaymentRequest paymentRequest, UserPrincipal userPrincipal) {

        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findOrdersByIdempotencyKey(paymentRequest.getIdempotencyKey(), member.getId()).orElseThrow(EntityNotFoundException::new);


        tryUpdateQuantityTwice(orders.getOrderProducts());

        PaymentInitStrategy paymentInitStrategy = paymentInitStrategyFactory.getInitStrategy(paymentRequest.getEventType());

        try {
            Payment payment = paymentInitStrategy.getPayment(paymentRequest, member, orders);
            return new PaymentResponse(payment.getId(), orders.getOrderProductSummary(), payment.getTotalPayAmount(), userPrincipal.getUsername());

        } catch (Exception exception) {
            deadLetterQueue(orders.getOrderProducts());
            throw exception;
        }
    }

    public void tryUpdateQuantityTwice(List<OrderProduct> orderProductList) {
        try {
            productService.updateQuantity(orderProductList);
        } catch (ObjectOptimisticLockingFailureException e) {
            productService.updateQuantity(orderProductList);
        }
    }

    public void deadLetterQueue(List<OrderProduct> orderProductList) {
        //큐에 주문상품을 넣어 비동기로 재고 복구
    }


}
