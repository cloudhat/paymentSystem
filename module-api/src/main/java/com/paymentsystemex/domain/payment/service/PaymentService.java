package com.paymentsystemex.domain.payment.service;

import core.domain.payment.repository.PaymentRepository;
import com.paymentsystemex.global.auth.AuthenticationException;
import com.paymentsystemex.global.auth.principal.UserPrincipal;
import core.domain.member.entity.Member;
import core.domain.order.entity.OrderPriceHistory;
import core.domain.order.entity.OrderProduct;
import core.domain.order.entity.OrderStatus;
import core.domain.order.entity.Orders;
import core.domain.payment.entity.Payment;
import core.domain.payment.entity.PaymentMethod;
import com.paymentsystemex.domain.payment.dto.PaymentInitResponse;
import core.domain.payment.dto.PaymentRequest;
import core.domain.member.repository.MemberRepository;
import core.domain.order.repository.OrderRepository;
import com.paymentsystemex.global.messageQueue.DeadLetterQueueService;
import com.paymentsystemex.domain.product.service.ProductService;
import com.paymentsystemex.domain.payment.service.payService.PayService;
import com.paymentsystemex.domain.payment.service.payService.PayServiceFactory;
import com.paymentsystemex.domain.payment.service.paymentInitStrategy.PaymentInitStrategy;
import com.paymentsystemex.domain.payment.service.paymentInitStrategy.PaymentInitStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentInitStrategyFactory paymentInitStrategyFactory;
    private final PayServiceFactory payServiceFactory;

    private final ProductService productService;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final DeadLetterQueueService deadLetterQueueService;

    @Transactional
    public PaymentInitResponse initPayment(PaymentRequest paymentRequest, UserPrincipal userPrincipal) {

        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findOrdersByIdempotencyKey(paymentRequest.getIdempotencyKey(), member.getId()).orElseThrow(EntityNotFoundException::new);

        if (!orders.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT)) {
            throw new IllegalArgumentException("Init payment is not available");
        }

        if (orders.getNormalPayment() != null) {
            Payment payment = orders.getNormalPayment();
            return new PaymentInitResponse(payment.getId(), orders.getOrderProductSummary(), payment.getTotalPayAmount(), userPrincipal.getUsername());
        }

        tryUpdateQuantityTwice(orders.getOrderProducts());

        PaymentInitStrategy paymentInitStrategy = paymentInitStrategyFactory.getInitStrategy(paymentRequest.getEventType());

        try {
            Payment payment = paymentInitStrategy.getPayment(paymentRequest, member, orders);
            return new PaymentInitResponse(payment.getId(), orders.getOrderProductSummary(), payment.getTotalPayAmount(), userPrincipal.getUsername());

        } catch (Exception exception) {
            deadLetterQueueService.enqueue(orders.getOrderProducts());
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

    @Transactional
    public Payment initTransaction(Long paymentId, Long memberId, String paykey) {
        Payment payment = paymentRepository.findPaymentById(paymentId, memberId).orElseThrow(EntityNotFoundException::new);
        payment.changeStatusToStart(paykey);

        return payment;
    }

    public void requestTransaction(PaymentMethod paymentMethod, Long paymentId, String payKey, int totalPayAmount) throws Exception {
        PayService payService = payServiceFactory.getPayService(paymentMethod);
        payService.requestTransaction(paymentId, payKey, totalPayAmount);
    }

    @Transactional
    public void completeTransaction(Long paymentId, Long memberId) {

        try {
            Payment payment = paymentRepository.findPaymentById(paymentId, memberId).orElseThrow(EntityNotFoundException::new);
            payment.changeStatusToComplete();
        } catch (Exception e) {
            deadLetterQueueService.enqueue(paymentId);
            throw e;
        }
    }

    @Transactional
    public OrderPriceHistory initCancel(Orders orders) {
        Payment payment = orders.getNormalPayment();
        PaymentInitStrategy paymentInitStrategy = paymentInitStrategyFactory.getInitStrategy(payment.getEventType());

        return paymentInitStrategy.initCancel(orders);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requestCancelTransaction(PaymentMethod paymentMethod, Long paymentId, String payKey, int totalPayAmount) throws Exception {
        PayService payService = payServiceFactory.getPayService(paymentMethod);
        payService.requestCancelTransaction(paymentId, payKey, totalPayAmount);
    }

}
