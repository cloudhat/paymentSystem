package com.paymentsystemex.service.payment;

import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.order.OrderStatus;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.domain.payment.PaymentMethod;
import com.paymentsystemex.dto.payment.PaymentInitResponse;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.OrderRepository;
import com.paymentsystemex.service.DeadLetterQueueService;
import com.paymentsystemex.service.ProductService;
import com.paymentsystemex.service.payment.payService.PayService;
import com.paymentsystemex.service.payment.payService.PayServiceFactory;
import com.paymentsystemex.service.payment.paymentInitStrategy.PaymentInitStrategy;
import com.paymentsystemex.service.payment.paymentInitStrategy.PaymentInitStrategyFactory;
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
    private final DeadLetterQueueService deadLetterQueueService;

    @Transactional
    public PaymentInitResponse initPayment(PaymentRequest paymentRequest, UserPrincipal userPrincipal) {

        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Orders orders = orderRepository.findOrdersByIdempotencyKey(paymentRequest.getIdempotencyKey(), member.getId()).orElseThrow(EntityNotFoundException::new);

        if (!orders.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT)) {
            throw new IllegalArgumentException("Init payment is not available");
        }

        if(orders.getNormalPayment()!= null){
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
        Payment payment = orderRepository.findPaymentById(paymentId, memberId).orElseThrow(EntityNotFoundException::new);
        payment.changeStatusToStart(paykey);

        return payment;
    }

    public void requestTransaction(PaymentMethod paymentMethod, Long paymentId, String payKey, int totalPayAmount) throws Exception {
        PayService payService = payServiceFactory.getPayService(paymentMethod);
        payService.requestTransaction(paymentId, payKey, totalPayAmount);
    }

    @Transactional
    public void completeTransaction(Long paymentId, Long memberId) {

        try{
            Payment payment = orderRepository.findPaymentById(paymentId, memberId).orElseThrow(EntityNotFoundException::new);
            payment.changeStatusToComplete();
        }catch (Exception e){
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
